package com.cafearoma.pedidos.model;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Categorías del menú y las opciones que el reto permite en cada una.
 * Es el ÚNICO lugar del código donde se definen las combinaciones válidas
 * (tabla "Menú" del documento Reto-Pedidos-Cafe-Aroma.md).
 */
public enum Category {
    BEBIDA_CALIENTE("Bebidas calientes",
            EnumSet.of(Size.PEQUENO, Size.MEDIANO, Size.GRANDE),
            EnumSet.of(Modifier.LECHE_DESLACTOSADA, Modifier.SHOT_EXTRA, Modifier.SIN_AZUCAR)),
    BEBIDA_FRIA("Bebidas frías",
            EnumSet.of(Size.MEDIANO, Size.GRANDE),
            EnumSet.of(Modifier.LECHE_DESLACTOSADA, Modifier.SHOT_EXTRA)),
    PANADERIA("Panadería",
            EnumSet.noneOf(Size.class),
            EnumSet.of(Modifier.PARA_LLEVAR, Modifier.CONSUMIR_EN_LOCAL));

    private final String label;
    private final Set<Size> allowedSizes;
    private final Set<Modifier> allowedModifiers;

    Category(String label, EnumSet<Size> allowedSizes, EnumSet<Modifier> allowedModifiers) {
        this.label = label;
        this.allowedSizes = Collections.unmodifiableSet(allowedSizes);
        this.allowedModifiers = Collections.unmodifiableSet(allowedModifiers);
    }

    public String getLabel() {
        return label;
    }

    /** Tamaños permitidos, en orden de menor a mayor. Vacío = tamaño único. */
    public Set<Size> getAllowedSizes() {
        return allowedSizes;
    }

    public Set<Modifier> getAllowedModifiers() {
        return allowedModifiers;
    }

    /** Panadería tiene tamaño único: no se elige tamaño. */
    public boolean usesSizes() {
        return !allowedSizes.isEmpty();
    }

    /**
     * Recargo de un tamaño respecto al tamaño MÁS PEQUEÑO disponible en la categoría.
     * Así el precio base de un producto es siempre su opción más barata
     * (Pequeño en bebidas calientes, Mediano en bebidas frías).
     */
    public BigDecimal sizeSurcharge(Size size) {
        Size smallest = allowedSizes.iterator().next();
        return size.getSurcharge().subtract(smallest.getSurcharge());
    }
}
