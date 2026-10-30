package com.cafearoma.pedidos.dto;

import com.cafearoma.pedidos.model.Modifier;
import com.cafearoma.pedidos.model.Size;

import java.util.List;

/** Un ítem del pedido: producto, tamaño (null en panadería) y 0..n modificadores. */
public record OrderItemRequest(Long productId, Size size, List<Modifier> modifiers) {
}
