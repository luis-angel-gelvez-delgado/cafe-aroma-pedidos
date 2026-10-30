package com.cafearoma.pedidos.model;

import java.math.BigDecimal;

/**
 * Tamaños de bebida. El recargo es un SUPUESTO documentado (el reto no define precios).
 * El recargo real de un ítem se calcula con {@link Category#sizeSurcharge(Size)}.
 */
public enum Size {
    PEQUENO("Pequeño", "0"),
    MEDIANO("Mediano", "1500"),
    GRANDE("Grande", "3000");

    private final String label;
    private final BigDecimal surcharge;

    Size(String label, String surcharge) {
        this.label = label;
        this.surcharge = new BigDecimal(surcharge);
    }

    public String getLabel() {
        return label;
    }

    public BigDecimal getSurcharge() {
        return surcharge;
    }
}
