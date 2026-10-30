package com.cafearoma.pedidos.dto;

import java.math.BigDecimal;
import java.util.Locale;

/** Una opción (tamaño o modificador) válida para un producto, con su recargo. */
public record OptionResponse(String code, String label, BigDecimal surcharge, String displayLabel) {

    public static OptionResponse of(String code, String label, BigDecimal surcharge) {
        String display = surcharge.signum() > 0 ? label + " (+$" + formatPesos(surcharge) + ")" : label;
        return new OptionResponse(code, label, surcharge, display);
    }

    /** 1500 → "1.500" (pesos colombianos, sin decimales). */
    static String formatPesos(BigDecimal value) {
        return String.format(Locale.ROOT, "%,d", value.longValue()).replace(',', '.');
    }
}
