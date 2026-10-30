package com.cafearoma.pedidos.service;

/** Se viola una regla de negocio del reto (la API responde 400). */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
