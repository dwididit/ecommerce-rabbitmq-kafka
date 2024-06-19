package dev.dwidi.ecommercerabbitmqkafka.controller;

import dev.dwidi.ecommercerabbitmqkafka.dto.PublicResponseDTO;
import dev.dwidi.ecommercerabbitmqkafka.dto.product.ProductRequestDTO;
import dev.dwidi.ecommercerabbitmqkafka.dto.product.ProductResponseDTO;
import dev.dwidi.ecommercerabbitmqkafka.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Operation(summary = "Create a product", description = "Create a new product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product created successfully"),
            @ApiResponse(responseCode = "403", description = "Access forbidden")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/create")
    public PublicResponseDTO<ProductResponseDTO> createProduct(
            @RequestBody ProductRequestDTO productRequestDTO) {
        return productService.createProduct(productRequestDTO);
    }

    @Operation(summary = "Update a product", description = "Update an existing product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "403", description = "Access forbidden")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/update")
    public PublicResponseDTO<ProductResponseDTO> updateProduct(
            @Parameter(description = "ID of the product to be updated") @RequestParam Long productId,
            @RequestBody ProductRequestDTO productRequestDTO) {
        return productService.updateProduct(productId, productRequestDTO);
    }

    @Operation(summary = "Delete a product", description = "Delete an existing product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "403", description = "Access forbidden")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/delete")
    public PublicResponseDTO<ProductResponseDTO> deleteProduct(
            @Parameter(description = "ID of the product to be deleted") @RequestParam Long productId) {
        return productService.deleteProduct(productId);
    }

    @Operation(summary = "Get all products", description = "Retrieve all products with optional pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access forbidden")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/all")
    public PublicResponseDTO<List<ProductResponseDTO>> getAllProducts(
            @Parameter(description = "Page number for pagination") @RequestParam(value = "page", required = false) Integer page,
            @Parameter(description = "Page size for pagination") @RequestParam(value = "size", required = false) Integer size) {
        return productService.getAllProducts(page, size);
    }

    @Operation(summary = "Get a product by ID", description = "Retrieve a specific product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "403", description = "Access forbidden")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public PublicResponseDTO<ProductResponseDTO> getProductById(
            @Parameter(description = "ID of the product to be retrieved") @RequestParam Long id) {
        return productService.getProductById(id);
    }
}
