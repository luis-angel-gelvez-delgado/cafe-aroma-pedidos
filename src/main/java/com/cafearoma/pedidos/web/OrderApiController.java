package com.cafearoma.pedidos.web;

import com.cafearoma.pedidos.dto.CreateOrderRequest;
import com.cafearoma.pedidos.dto.OrderResponse;
import com.cafearoma.pedidos.dto.UpdatePaymentRequest;
import com.cafearoma.pedidos.dto.UpdateStatusRequest;
import com.cafearoma.pedidos.model.OrderStatus;
import com.cafearoma.pedidos.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/** API REST de pedidos. No tiene reglas de negocio: solo delega en OrderService. */
@RestController
@RequestMapping("/api/orders")
public class OrderApiController {

    private final OrderService orderService;

    public OrderApiController(OrderService orderService) {
        this.orderService = orderService;
    }

    /** POST /api/orders → 201 Created con el pedido y su total calculado. */
    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody CreateOrderRequest request) {
        OrderResponse created = orderService.createOrder(request);
        return ResponseEntity.created(URI.create("/api/orders/" + created.id())).body(created);
    }

    /** GET /api/orders?status=RECIBIDO → pedidos filtrados por estado (sin filtro: todos). */
    @GetMapping
    public List<OrderResponse> list(@RequestParam(name = "status", required = false) OrderStatus status) {
        return orderService.findOrders(status);
    }

    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable("id") Long id) {
        return orderService.getOrder(id);
    }

    /** PATCH /api/orders/{id}/status → 409 si la transición no está permitida. */
    @PatchMapping("/{id}/status")
    public OrderResponse changeStatus(@PathVariable("id") Long id, @RequestBody UpdateStatusRequest request) {
        return orderService.changeStatus(id, request.status());
    }

    @PatchMapping("/{id}/payment")
    public OrderResponse updatePayment(@PathVariable("id") Long id, @RequestBody UpdatePaymentRequest request) {
        return orderService.updatePayment(id, request.paymentStatus());
    }
}
