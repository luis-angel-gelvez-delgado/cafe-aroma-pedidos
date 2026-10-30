package com.cafearoma.pedidos.dto;

import com.cafearoma.pedidos.model.PaymentStatus;

public record UpdatePaymentRequest(PaymentStatus paymentStatus) {
}
