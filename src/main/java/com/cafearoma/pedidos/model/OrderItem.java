package com.cafearoma.pedidos.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Un producto dentro de un pedido, con su tamaño (si aplica) y sus modificadores.
 * "price" es el precio APLICADO en el momento del pedido: si después cambia el
 * catálogo, los pedidos históricos conservan el precio que se cobró.
 */
@Entity
@Table(name = "order_item")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private CustomerOrder order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /** Null para productos de tamaño único (panadería). */
    @Enumerated(EnumType.STRING)
    @Column(name = "item_size", length = 10)
    private Size size;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "order_item_modifier", joinColumns = @JoinColumn(name = "order_item_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "modifier", nullable = false, length = 25)
    private Set<Modifier> modifiers = new HashSet<>();

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    protected OrderItem() {
        // requerido por JPA
    }

    public OrderItem(Product product, Size size, Set<Modifier> modifiers, BigDecimal price) {
        this.product = product;
        this.size = size;
        this.modifiers.addAll(modifiers);
        this.price = price;
    }

    void setOrder(CustomerOrder order) {
        this.order = order;
    }

    public Long getId() {
        return id;
    }

    public CustomerOrder getOrder() {
        return order;
    }

    public Product getProduct() {
        return product;
    }

    public Size getSize() {
        return size;
    }

    public Set<Modifier> getModifiers() {
        return modifiers;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
