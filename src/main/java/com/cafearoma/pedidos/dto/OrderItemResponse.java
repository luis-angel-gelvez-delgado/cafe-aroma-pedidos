package com.cafearoma.pedidos.dto;

import com.cafearoma.pedidos.model.Modifier;
import com.cafearoma.pedidos.model.OrderItem;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

/** Ítem de un pedido listo para mostrar (tamaño y modificadores como texto). */
public record OrderItemResponse(String productName, String size, List<String> modifiers, BigDecimal price) {

    public static OrderItemResponse from(OrderItem item) {
        List<String> modifiers = item.getModifiers().stream()
                .sorted(Comparator.comparingInt(Modifier::ordinal))
                .map(Modifier::getLabel)
                .toList();
        String size = item.getSize() == null ? null : item.getSize().getLabel();
        return new OrderItemResponse(item.getProduct().getName(), size, modifiers, item.getPrice());
    }
}
