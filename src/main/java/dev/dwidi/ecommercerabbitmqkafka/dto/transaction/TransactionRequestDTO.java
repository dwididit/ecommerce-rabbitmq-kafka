package dev.dwidi.ecommercerabbitmqkafka.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequestDTO {
    private Long userId;
    private Long productId;
    private Integer productQuantity;
}
