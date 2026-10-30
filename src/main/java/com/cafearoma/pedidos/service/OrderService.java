package com.cafearoma.pedidos.service;

import com.cafearoma.pedidos.dto.CreateOrderRequest;
import com.cafearoma.pedidos.dto.OrderItemRequest;
import com.cafearoma.pedidos.dto.OrderResponse;
import com.cafearoma.pedidos.model.Category;
import com.cafearoma.pedidos.model.CustomerOrder;
import com.cafearoma.pedidos.model.Modifier;
import com.cafearoma.pedidos.model.OrderItem;
import com.cafearoma.pedidos.model.OrderStatus;
import com.cafearoma.pedidos.model.PaymentStatus;
import com.cafearoma.pedidos.model.Product;
import com.cafearoma.pedidos.model.Size;
import com.cafearoma.pedidos.repository.CustomerOrderRepository;
import com.cafearoma.pedidos.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Reglas de negocio de los pedidos. La API REST y la pantalla Thymeleaf
 * usan este mismo servicio, así las reglas existen en un solo lugar.
 */
@Service
@Transactional
public class OrderService {

    static final int MAX_CUSTOMER_NAME_LENGTH = 60;

    private final ProductRepository productRepository;
    private final CustomerOrderRepository orderRepository;

    public OrderService(ProductRepository productRepository, CustomerOrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    public OrderResponse createOrder(CreateOrderRequest request) {
        if (request == null) {
            throw new BusinessRuleException("La solicitud del pedido está vacía.");
        }
        String customerName = request.customerName() == null ? "" : request.customerName().trim();
        if (customerName.isEmpty()) {
            throw new BusinessRuleException("El nombre del cliente es obligatorio: se usa para llamar el pedido.");
        }
        if (customerName.length() > MAX_CUSTOMER_NAME_LENGTH) {
            throw new BusinessRuleException("El nombre del cliente admite máximo "
                    + MAX_CUSTOMER_NAME_LENGTH + " caracteres.");
        }
        if (request.items() == null || request.items().isEmpty()) {
            throw new BusinessRuleException("El pedido debe tener al menos un producto.");
        }

        CustomerOrder order = new CustomerOrder(customerName);
        for (OrderItemRequest itemRequest : request.items()) {
            if (itemRequest == null || itemRequest.productId() == null) {
                throw new BusinessRuleException("Cada ítem debe indicar un producto del menú.");
            }
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new BusinessRuleException(
                            "El producto con id " + itemRequest.productId() + " no existe en el menú."));
            Set<Modifier> modifiers = toModifierSet(itemRequest.modifiers());
            BigDecimal price = calculateItemPrice(product, itemRequest.size(), modifiers);
            order.addItem(new OrderItem(product, itemRequest.size(), modifiers, price));
        }
        return OrderResponse.from(orderRepository.save(order));
    }

    /**
     * Valida el ítem contra las opciones de su categoría y calcula su precio:
     * precio base + recargo del tamaño + recargo de cada modificador.
     */
    static BigDecimal calculateItemPrice(Product product, Size size, Set<Modifier> modifiers) {
        Category category = product.getCategory();

        if (category.usesSizes()) {
            if (size == null) {
                throw new BusinessRuleException("Debes elegir un tamaño para " + product.getName() + ".");
            }
            if (!category.getAllowedSizes().contains(size)) {
                throw new BusinessRuleException("El tamaño " + size.getLabel()
                        + " no está disponible para " + product.getName() + ".");
            }
        } else if (size != null) {
            throw new BusinessRuleException(product.getName() + " es de tamaño único: no lleva tamaño.");
        }

        for (Modifier modifier : modifiers) {
            if (!category.getAllowedModifiers().contains(modifier)) {
                throw new BusinessRuleException("«" + modifier.getLabel() + "» no es válido para "
                        + product.getName() + ".");
            }
        }
        if (modifiers.contains(Modifier.PARA_LLEVAR) && modifiers.contains(Modifier.CONSUMIR_EN_LOCAL)) {
            throw new BusinessRuleException("Elige «Para llevar» o «Para consumir en el local», no ambos.");
        }

        BigDecimal price = product.getBasePrice();
        if (size != null) {
            price = price.add(category.sizeSurcharge(size));
        }
        for (Modifier modifier : modifiers) {
            price = price.add(modifier.getSurcharge());
        }
        return price;
    }

    private static Set<Modifier> toModifierSet(List<Modifier> modifiers) {
        Set<Modifier> result = EnumSet.noneOf(Modifier.class);
        if (modifiers == null) {
            return result;
        }
        for (Modifier modifier : modifiers) {
            if (modifier == null) {
                throw new BusinessRuleException("La lista de modificadores contiene un valor vacío.");
            }
            result.add(modifier);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findOrders(OrderStatus status) {
        List<CustomerOrder> orders = status == null
                ? orderRepository.findAllByOrderByIdDesc()
                : orderRepository.findByStatusOrderByIdAsc(status);
        return orders.stream().map(OrderResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findActiveOrders() {
        return orderRepository.findByStatusInOrderByIdAsc(OrderStatus.activeStatuses()).stream()
                .map(OrderResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findClosedOrders() {
        return orderRepository.findByStatusInOrderByIdDesc(OrderStatus.closedStatuses()).stream()
                .map(OrderResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long id) {
        return OrderResponse.from(findOrderEntity(id));
    }

    /** Las transiciones se validan aquí, en el backend, no solo en la pantalla (regla del reto). */
    public OrderResponse changeStatus(Long id, OrderStatus newStatus) {
        if (newStatus == null) {
            throw new BusinessRuleException("Debes indicar el nuevo estado del pedido.");
        }
        CustomerOrder order = findOrderEntity(id);
        if (!order.getStatus().canTransitionTo(newStatus)) {
            throw new InvalidStatusTransitionException(order.getStatus(), newStatus);
        }
        order.setStatus(newStatus);
        return OrderResponse.from(order);
    }

    public OrderResponse updatePayment(Long id, PaymentStatus paymentStatus) {
        if (paymentStatus == null) {
            throw new BusinessRuleException("Debes indicar el estado del pago (PENDIENTE o PAGADO).");
        }
        CustomerOrder order = findOrderEntity(id);
        if (order.getStatus() == OrderStatus.CANCELADO) {
            throw new BusinessRuleException("No se puede registrar el pago de un pedido cancelado.");
        }
        order.setPaymentStatus(paymentStatus);
        return OrderResponse.from(order);
    }

    private CustomerOrder findOrderEntity(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe el pedido con id " + id + "."));
    }
}
