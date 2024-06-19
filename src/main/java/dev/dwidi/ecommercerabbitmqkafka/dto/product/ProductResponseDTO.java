package dev.dwidi.ecommercerabbitmqkafka.dto.product;

import dev.dwidi.ecommercerabbitmqkafka.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDTO {
    private Long id;
    private String productName;
    private String productDescription;
    private String productCategory;
    private Double productPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProductResponseDTO(Product product) {
        this.id = product.getId();
        this.productName = product.getProductName();
        this.productDescription = product.getProductDescription();
        this.productCategory = product.getProductCategory();
        this.productPrice = product.getProductPrice();
        this.createdAt = product.getCreatedAt();
        this.updatedAt = product.getUpdatedAt();
    }
}
