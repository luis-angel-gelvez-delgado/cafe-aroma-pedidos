package com.cafearoma.pedidos.service;

import com.cafearoma.pedidos.dto.CreateOrderRequest;
import com.cafearoma.pedidos.dto.OrderItemRequest;
import com.cafearoma.pedidos.dto.OrderResponse;
import com.cafearoma.pedidos.model.Modifier;
import com.cafearoma.pedidos.model.OrderStatus;
import com.cafearoma.pedidos.model.PaymentStatus;
import com.cafearoma.pedidos.model.Size;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Prueba de integración: levanta Spring con una base H2 en memoria (perfil "test"),
 * ejecuta schema.sql y data.sql, y prueba el servicio con persistencia real.
 * @Transactional deshace los cambios al final de cada prueba.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrderServiceIntegrationTest {

    // ids de data.sql
    private static final long LATTE = 4L;
    private static final long FRAPPE = 6L;
    private static final long CROISSANT = 8L;

    @Autowired
    private OrderService orderService;

    @Test
    void creaPedidoValidoYCalculaElTotal() {
        OrderResponse created = orderService.createOrder(new CreateOrderRequest("Ana", List.of(
                new OrderItemRequest(LATTE, Size.GRANDE, List.of(Modifier.SHOT_EXTRA, Modifier.LECHE_DESLACTOSADA)),
                new OrderItemRequest(CROISSANT, null, List.of(Modifier.PARA_LLEVAR)))));

        // 13000 (latte grande + shot + deslactosada) + 5000 (croissant)
        assertEquals(0, new BigDecimal("18000").compareTo(created.total()));
        assertEquals(OrderStatus.RECIBIDO, created.status());
        assertEquals(PaymentStatus.PENDIENTE, created.paymentStatus());

        OrderResponse reloaded = orderService.getOrder(created.id());
        assertEquals(2, reloaded.items().size());
        assertEquals("Ana", reloaded.customerName());
    }

    @Test
    void rechazaPedidoSinItems() {
        assertThrows(BusinessRuleException.class,
                () -> orderService.createOrder(new CreateOrderRequest("Ana", List.of())));
    }

    @Test
    void rechazaNombreDeClienteVacio() {
        assertThrows(BusinessRuleException.class, () -> orderService.createOrder(new CreateOrderRequest("   ",
                List.of(new OrderItemRequest(CROISSANT, null, List.of())))));
    }

    @Test
    void rechazaModificadorNoValidoParaElProducto() {
        assertThrows(BusinessRuleException.class, () -> orderService.createOrder(new CreateOrderRequest("Luis",
                List.of(new OrderItemRequest(CROISSANT, null, List.of(Modifier.SHOT_EXTRA))))));
    }

    @Test
    void rechazaTamanoIncompatible() {
        assertThrows(BusinessRuleException.class, () -> orderService.createOrder(new CreateOrderRequest("Luis",
                List.of(new OrderItemRequest(FRAPPE, Size.PEQUENO, List.of())))));
    }

    @Test
    void rechazaProductoInexistente() {
        assertThrows(BusinessRuleException.class, () -> orderService.createOrder(new CreateOrderRequest("Luis",
                List.of(new OrderItemRequest(999L, null, List.of())))));
    }

    @Test
    void recorreElFlujoCompletoHastaEntregado() {
        Long id = createSimpleOrder();
        orderService.changeStatus(id, OrderStatus.EN_PREPARACION);
        orderService.changeStatus(id, OrderStatus.LISTO_PARA_RECOGER);
        OrderResponse delivered = orderService.changeStatus(id, OrderStatus.ENTREGADO);
        assertEquals(OrderStatus.ENTREGADO, delivered.status());
    }

    @Test
    void rechazaSaltarseEstados() {
        Long id = createSimpleOrder();
        assertThrows(InvalidStatusTransitionException.class,
                () -> orderService.changeStatus(id, OrderStatus.ENTREGADO));
    }

    @Test
    void permiteCancelarEnPreparacion() {
        Long id = createSimpleOrder();
        orderService.changeStatus(id, OrderStatus.EN_PREPARACION);
        assertEquals(OrderStatus.CANCELADO, orderService.changeStatus(id, OrderStatus.CANCELADO).status());
    }

    @Test
    void rechazaCancelarCuandoEstaListoParaRecoger() {
        Long id = createSimpleOrder();
        orderService.changeStatus(id, OrderStatus.EN_PREPARACION);
        orderService.changeStatus(id, OrderStatus.LISTO_PARA_RECOGER);
        assertThrows(InvalidStatusTransitionException.class,
                () -> orderService.changeStatus(id, OrderStatus.CANCELADO));
    }

    @Test
    void registraElPago() {
        Long id = createSimpleOrder();
        assertTrue(orderService.updatePayment(id, PaymentStatus.PAGADO).paid());
    }

    @Test
    void noRegistraPagoDePedidoCancelado() {
        Long id = createSimpleOrder();
        orderService.changeStatus(id, OrderStatus.CANCELADO);
        assertThrows(BusinessRuleException.class, () -> orderService.updatePayment(id, PaymentStatus.PAGADO));
    }

    @Test
    void consultaPedidosPorEstado() {
        Long id = createSimpleOrder();
        orderService.changeStatus(id, OrderStatus.EN_PREPARACION);
        List<OrderResponse> inPreparation = orderService.findOrders(OrderStatus.EN_PREPARACION);
        assertTrue(inPreparation.stream().anyMatch(o -> o.id().equals(id)));
        assertTrue(orderService.findOrders(OrderStatus.RECIBIDO).stream().noneMatch(o -> o.id().equals(id)));
    }

    @Test
    void lanzaNotFoundSiElPedidoNoExiste() {
        assertThrows(NotFoundException.class, () -> orderService.getOrder(99999L));
    }

    private Long createSimpleOrder() {
        return orderService.createOrder(new CreateOrderRequest("Cliente prueba",
                List.of(new OrderItemRequest(CROISSANT, null, List.of())))).id();
    }
}
