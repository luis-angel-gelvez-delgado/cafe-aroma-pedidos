package com.cafearoma.pedidos.service;

import com.cafearoma.pedidos.model.OrderStatus;

/** Transición de estado no permitida (la API responde 409 Conflict). */
public class InvalidStatusTransitionException extends BusinessRuleException {

    public InvalidStatusTransitionException(OrderStatus from, OrderStatus to) {
        super(buildMessage(from, to));
    }

    private static String buildMessage(OrderStatus from, OrderStatus to) {
        String message = "No se puede pasar un pedido de «" + from.getLabel() + "» a «" + to.getLabel() + "».";
        if (to == OrderStatus.CANCELADO) {
            message += " Solo se puede cancelar en «Recibido» o «En preparación».";
        }
        return message;
    }
}
