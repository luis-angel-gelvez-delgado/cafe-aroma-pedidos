package com.cafearoma.pedidos.dto;

import com.cafearoma.pedidos.model.OrderStatus;

public record UpdateStatusRequest(OrderStatus status) {
}
