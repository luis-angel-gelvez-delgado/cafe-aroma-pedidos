package com.cafearoma.pedidos.web;

import com.cafearoma.pedidos.dto.ProductResponse;
import com.cafearoma.pedidos.service.CatalogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductApiController {

    private final CatalogService catalogService;

    public ProductApiController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    /** GET /api/products → menú con las opciones válidas de cada producto. */
    @GetMapping
    public List<ProductResponse> list() {
        return catalogService.findAll();
    }
}
