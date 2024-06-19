package dev.dwidi.ecommercerabbitmqkafka.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDTO {
    private String productName;
    private String productDescription;
    private String productCategory;
    private Double productPrice;
}
