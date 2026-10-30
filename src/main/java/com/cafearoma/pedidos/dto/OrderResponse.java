package com.cafearoma.pedidos.dto;

import com.cafearoma.pedidos.model.CustomerOrder;
import com.cafearoma.pedidos.model.OrderStatus;
import com.cafearoma.pedidos.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Pedido tal como lo ven la API y la pantalla.
 * nextStatus y cancellable salen de OrderStatus, así la interfaz no repite las reglas.
 */
public record OrderResponse(
        Long id,
        String orderNumber,
        String customerName,
        OrderStatus status,
        String statusLabel,
        PaymentStatus paymentStatus,
        String paymentStatusLabel,
        boolean paid,
        BigDecimal total,
        LocalDateTime createdAt,
        List<OrderItemResponse> items,
        OrderStatus nextStatus,
        String nextStatusLabel,
        boolean cancellable) {

    public static OrderResponse from(CustomerOrder order) {
        OrderStatus status = order.getStatus();
        OrderStatus next = status.nextStep();
        return new OrderResponse(
                order.getId(),
                "#" + String.format("%04d", order.getId()),
                order.getCustomerName(),
                status,
                status.getLabel(),
                order.getPaymentStatus(),
                order.getPaymentStatus().getLabel(),
                order.getPaymentStatus() == PaymentStatus.PAGADO,
                order.getTotal(),
                order.getCreatedAt(),
                order.getItems().stream().map(OrderItemResponse::from).toList(),
                next,
                next == null ? null : next.getLabel(),
                status.isCancellable());
    }
}
