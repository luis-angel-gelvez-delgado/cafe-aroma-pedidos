package com.cafearoma.pedidos.model;

import java.math.BigDecimal;

/**
 * Modificadores del menú. Los precios son SUPUESTOS documentados en requirements.md.
 * Qué modificador admite cada producto lo decide {@link Category}.
 */
public enum Modifier {
    LECHE_DESLACTOSADA("Leche deslactosada", "1000"),
    SHOT_EXTRA("Shot extra de café", "2000"),
    SIN_AZUCAR("Sin azúcar", "0"),
    PARA_LLEVAR("Para llevar", "0"),
    CONSUMIR_EN_LOCAL("Para consumir en el local", "0");

    private final String label;
    private final BigDecimal surcharge;

    Modifier(String label, String surcharge) {
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
