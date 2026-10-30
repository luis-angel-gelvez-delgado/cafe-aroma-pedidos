package com.cafearoma.pedidos.model;

import java.util.List;

/**
 * Estados del pedido y transiciones permitidas.
 * Recibido → En preparación → Listo para recoger → Entregado.
 * Cancelado solo desde Recibido o En preparación (regla del reto).
 */
public enum OrderStatus {
    RECIBIDO("Recibido"),
    EN_PREPARACION("En preparación"),
    LISTO_PARA_RECOGER("Listo para recoger"),
    ENTREGADO("Entregado"),
    CANCELADO("Cancelado");

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public boolean canTransitionTo(OrderStatus next) {
        return switch (this) {
            case RECIBIDO -> next == EN_PREPARACION || next == CANCELADO;
            case EN_PREPARACION -> next == LISTO_PARA_RECOGER || next == CANCELADO;
            case LISTO_PARA_RECOGER -> next == ENTREGADO;
            case ENTREGADO, CANCELADO -> false;
        };
    }

    /** Siguiente paso del flujo normal, o null si el pedido ya terminó. */
    public OrderStatus nextStep() {
        return switch (this) {
            case RECIBIDO -> EN_PREPARACION;
            case EN_PREPARACION -> LISTO_PARA_RECOGER;
            case LISTO_PARA_RECOGER -> ENTREGADO;
            case ENTREGADO, CANCELADO -> null;
        };
    }

    public boolean isCancellable() {
        return canTransitionTo(CANCELADO);
    }

    public static List<OrderStatus> activeStatuses() {
        return List.of(RECIBIDO, EN_PREPARACION, LISTO_PARA_RECOGER);
    }

    public static List<OrderStatus> closedStatuses() {
        return List.of(ENTREGADO, CANCELADO);
    }
}
