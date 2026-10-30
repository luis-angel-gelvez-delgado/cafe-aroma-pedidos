package com.cafearoma.pedidos.service;

import com.cafearoma.pedidos.model.Category;
import com.cafearoma.pedidos.model.Modifier;
import com.cafearoma.pedidos.model.Product;
import com.cafearoma.pedidos.model.Size;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Prueba unitaria del cálculo y validación de un ítem (OrderService.calculateItemPrice).
 * Precios según los supuestos de requirements.md.
 */
class ItemPricingTest {

    private final Product latte = new Product("Latte", Category.BEBIDA_CALIENTE, new BigDecimal("7000"));
    private final Product frappe = new Product("Frappé de Café", Category.BEBIDA_FRIA, new BigDecimal("10000"));
    private final Product croissant = new Product("Croissant", Category.PANADERIA, new BigDecimal("5000"));

    @Test
    void bebidaCalientePequenaSinModificadoresCuestaElPrecioBase() {
        assertMoney("7000", OrderService.calculateItemPrice(latte, Size.PEQUENO, none()));
    }

    @Test
    void sumaTamanoYModificadores() {
        // 7000 + 3000 (grande) + 2000 (shot) + 1000 (deslactosada)
        BigDecimal price = OrderService.calculateItemPrice(latte, Size.GRANDE,
                EnumSet.of(Modifier.SHOT_EXTRA, Modifier.LECHE_DESLACTOSADA));
        assertMoney("13000", price);
    }

    @Test
    void bebidaFriaUsaMedianoComoBase() {
        assertMoney("10000", OrderService.calculateItemPrice(frappe, Size.MEDIANO, none()));
        assertMoney("11500", OrderService.calculateItemPrice(frappe, Size.GRANDE, none()));
    }

    @Test
    void panaderiaNoLlevaTamano() {
        assertMoney("5000", OrderService.calculateItemPrice(croissant, null, EnumSet.of(Modifier.PARA_LLEVAR)));
        assertThrows(BusinessRuleException.class,
                () -> OrderService.calculateItemPrice(croissant, Size.GRANDE, none()));
    }

    @Test
    void bebidaSinTamanoEsRechazada() {
        assertThrows(BusinessRuleException.class, () -> OrderService.calculateItemPrice(latte, null, none()));
    }

    @Test
    void tamanoPequenoNoExisteEnBebidasFrias() {
        assertThrows(BusinessRuleException.class,
                () -> OrderService.calculateItemPrice(frappe, Size.PEQUENO, none()));
    }

    @Test
    void modificadorDeOtraCategoriaEsRechazado() {
        assertThrows(BusinessRuleException.class,
                () -> OrderService.calculateItemPrice(croissant, null, EnumSet.of(Modifier.SHOT_EXTRA)));
        assertThrows(BusinessRuleException.class,
                () -> OrderService.calculateItemPrice(frappe, Size.MEDIANO, EnumSet.of(Modifier.SIN_AZUCAR)));
    }

    @Test
    void paraLlevarYConsumirEnLocalSonExcluyentes() {
        assertThrows(BusinessRuleException.class,
                () -> OrderService.calculateItemPrice(croissant, null,
                        EnumSet.of(Modifier.PARA_LLEVAR, Modifier.CONSUMIR_EN_LOCAL)));
    }

    private static Set<Modifier> none() {
        return EnumSet.noneOf(Modifier.class);
    }

    private static void assertMoney(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual),
                "Se esperaba " + expected + " pero fue " + actual);
    }
}
