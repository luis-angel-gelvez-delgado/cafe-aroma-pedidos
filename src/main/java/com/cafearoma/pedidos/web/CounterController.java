package com.cafearoma.pedidos.web;

import com.cafearoma.pedidos.dto.OrderResponse;
import com.cafearoma.pedidos.dto.ProductResponse;
import com.cafearoma.pedidos.model.OrderStatus;
import com.cafearoma.pedidos.model.PaymentStatus;
import com.cafearoma.pedidos.service.BusinessRuleException;
import com.cafearoma.pedidos.service.CatalogService;
import com.cafearoma.pedidos.service.NotFoundException;
import com.cafearoma.pedidos.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Pantalla del mostrador (Thymeleaf). Usa los mismos servicios que la API REST. */
@Controller
public class CounterController {

    private final CatalogService catalogService;
    private final OrderService orderService;

    public CounterController(CatalogService catalogService, OrderService orderService) {
        this.catalogService = catalogService;
        this.orderService = orderService;
    }

    @GetMapping("/")
    public String counter(@RequestParam(name = "nuevo", required = false) Long createdOrderId, Model model) {
        Map<String, List<ProductResponse>> menu = catalogService.findAll().stream()
                .collect(Collectors.groupingBy(ProductResponse::categoryLabel, LinkedHashMap::new, Collectors.toList()));

        Map<OrderStatus, List<OrderResponse>> board = new LinkedHashMap<>();
        for (OrderStatus status : OrderStatus.activeStatuses()) {
            board.put(status, new ArrayList<>());
        }
        for (OrderResponse order : orderService.findActiveOrders()) {
            board.get(order.status()).add(order);
        }

        model.addAttribute("menu", menu);
        model.addAttribute("board", board);
        model.addAttribute("closedOrders", orderService.findClosedOrders());
        if (createdOrderId != null) {
            try {
                model.addAttribute("createdOrder", orderService.getOrder(createdOrderId));
            } catch (NotFoundException e) {
                model.addAttribute("error", e.getMessage());
            }
        }
        return "counter";
    }

    @PostMapping("/orders/{id}/status")
    public String changeStatus(@PathVariable("id") Long id,
                               @RequestParam("status") OrderStatus status,
                               RedirectAttributes redirect) {
        try {
            OrderResponse order = orderService.changeStatus(id, status);
            redirect.addFlashAttribute("success",
                    "Pedido " + order.orderNumber() + " ahora está en «" + order.statusLabel() + "».");
        } catch (BusinessRuleException | NotFoundException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/";
    }

    @PostMapping("/orders/{id}/payment")
    public String updatePayment(@PathVariable("id") Long id,
                                @RequestParam("paymentStatus") PaymentStatus paymentStatus,
                                RedirectAttributes redirect) {
        try {
            OrderResponse order = orderService.updatePayment(id, paymentStatus);
            redirect.addFlashAttribute("success",
                    "Pago del pedido " + order.orderNumber() + ": " + order.paymentStatusLabel() + ".");
        } catch (BusinessRuleException | NotFoundException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/";
    }
}
