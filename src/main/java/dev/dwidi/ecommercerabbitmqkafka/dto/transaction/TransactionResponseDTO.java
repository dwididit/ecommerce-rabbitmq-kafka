package dev.dwidi.ecommercerabbitmqkafka.dto.transaction;

import dev.dwidi.ecommercerabbitmqkafka.entity.Transaction;
import dev.dwidi.ecommercerabbitmqkafka.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponseDTO {
    private Long id;
    private Long userId;
    private Long productId;
    private Integer productQuantity;
    private TransactionStatus status;
    private Double totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TransactionResponseDTO(Transaction transaction) {
        this.id = transaction.getId();
        this.userId = transaction.getUser().getId();
        this.productId = transaction.getProduct().getId();
        this.status = transaction.getStatus();
        this.totalAmount = transaction.getTotalAmount();
        this.createdAt = transaction.getCreatedAt();
        this.updatedAt = transaction.getUpdatedAt();
    }
}
