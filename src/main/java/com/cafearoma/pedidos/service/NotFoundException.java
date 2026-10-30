package com.cafearoma.pedidos.service;

/** El recurso pedido no existe (la API responde 404). */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
