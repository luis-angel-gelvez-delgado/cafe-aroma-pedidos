package com.cafearoma.pedidos.dto;

import com.cafearoma.pedidos.model.Category;
import com.cafearoma.pedidos.model.Product;

import java.math.BigDecimal;
import java.util.List;

/** Producto del menú con SOLO las opciones válidas para su categoría. */
public record ProductResponse(
        Long id,
        String name,
        Category category,
        String categoryLabel,
        BigDecimal basePrice,
        List<OptionResponse> sizes,
        List<OptionResponse> modifiers) {

    public static ProductResponse from(Product product) {
        Category category = product.getCategory();
        List<OptionResponse> sizes = category.getAllowedSizes().stream()
                .map(s -> OptionResponse.of(s.name(), s.getLabel(), category.sizeSurcharge(s)))
                .toList();
        List<OptionResponse> modifiers = category.getAllowedModifiers().stream()
                .map(m -> OptionResponse.of(m.name(), m.getLabel(), m.getSurcharge()))
                .toList();
        return new ProductResponse(product.getId(), product.getName(), category, category.getLabel(),
                product.getBasePrice(), sizes, modifiers);
    }
}
