package com.cafearoma.pedidos.model;

import org.junit.jupiter.api.Test;

import static com.cafearoma.pedidos.model.OrderStatus.CANCELADO;
import static com.cafearoma.pedidos.model.OrderStatus.EN_PREPARACION;
import static com.cafearoma.pedidos.model.OrderStatus.ENTREGADO;
import static com.cafearoma.pedidos.model.OrderStatus.LISTO_PARA_RECOGER;
import static com.cafearoma.pedidos.model.OrderStatus.RECIBIDO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Prueba unitaria: no necesita Spring ni base de datos. */
class OrderStatusTest {

    @Test
    void permiteElFlujoNormalPasoAPaso() {
        assertTrue(RECIBIDO.canTransitionTo(EN_PREPARACION));
        assertTrue(EN_PREPARACION.canTransitionTo(LISTO_PARA_RECOGER));
        assertTrue(LISTO_PARA_RECOGER.canTransitionTo(ENTREGADO));
    }

    @Test
    void noPermiteSaltarNiRetrocederEstados() {
        assertFalse(RECIBIDO.canTransitionTo(LISTO_PARA_RECOGER));
        assertFalse(RECIBIDO.canTransitionTo(ENTREGADO));
        assertFalse(LISTO_PARA_RECOGER.canTransitionTo(RECIBIDO));
        assertFalse(ENTREGADO.canTransitionTo(RECIBIDO));
    }

    @Test
    void cancelacionSoloDesdeRecibidoOEnPreparacion() {
        assertTrue(RECIBIDO.canTransitionTo(CANCELADO));
        assertTrue(EN_PREPARACION.canTransitionTo(CANCELADO));
        assertFalse(LISTO_PARA_RECOGER.canTransitionTo(CANCELADO));
        assertFalse(ENTREGADO.canTransitionTo(CANCELADO));
    }

    @Test
    void estadosFinalesNoTienenSiguientePaso() {
        assertEquals(EN_PREPARACION, RECIBIDO.nextStep());
        assertNull(ENTREGADO.nextStep());
        assertNull(CANCELADO.nextStep());
        assertFalse(CANCELADO.canTransitionTo(RECIBIDO));
    }
}
