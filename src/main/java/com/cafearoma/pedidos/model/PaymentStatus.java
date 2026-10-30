package com.cafearoma.pedidos.model;

/** El pago se registra en el mostrador; no hay pasarela de pago (regla 3 del reto). */
public enum PaymentStatus {
    PENDIENTE("Pendiente"),
    PAGADO("Pagado");

    private final String label;

    PaymentStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
