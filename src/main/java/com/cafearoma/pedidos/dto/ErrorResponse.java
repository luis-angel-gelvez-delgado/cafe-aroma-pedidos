package com.cafearoma.pedidos.dto;

/** Respuesta de error de la API: código HTTP + mensaje comprensible. */
public record ErrorResponse(int status, String message) {
}
