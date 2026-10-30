package com.cafearoma.pedidos.dto;

import java.util.List;

/** Datos para crear un pedido. El precio NO se recibe: lo calcula el backend. */
public record CreateOrderRequest(String customerName, List<OrderItemRequest> items) {
}
