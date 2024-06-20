package dev.dwidi.ecommercerabbitmqkafka.controller;

import dev.dwidi.ecommercerabbitmqkafka.dto.PublicResponseDTO;
import dev.dwidi.ecommercerabbitmqkafka.entity.Product;
import dev.dwidi.ecommercerabbitmqkafka.exception.UserNotFoundException;
import dev.dwidi.ecommercerabbitmqkafka.service.kafka.KafkaConsumerService;
import dev.dwidi.ecommercerabbitmqkafka.service.kafka.KafkaProducerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private KafkaConsumerService kafkaConsumerService;

    @Autowired
    private Supplier<String> uuidSupplier;

    @Operation(summary = "Get product recommendations", description = "Get product recommendations for a user based on purchase history")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recommendations fetched successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Failed to fetch recommendations"),
            @ApiResponse(responseCode = "403", description = "Access forbidden")
    })
    @PreAuthorize("@customSecurityExpression.isAdminOrSelf(#userId)")
    @PostMapping("/byUser")
    public PublicResponseDTO<List<Product>> recommendProducts(
            @Parameter(description = "ID of the user to get recommendations for") @RequestParam Long userId) {
        try {
            // Check if the user exists before sending the Kafka message
            kafkaConsumerService.checkUserExists(userId);

            // Proceed to send Kafka message
            kafkaProducerService.sendMessage(userId);

            // Wait for Kafka to process the message and produce recommendations
            List<Product> recommendations = null;
            for (int i = 0; i < 5; i++) {
                recommendations = kafkaConsumerService.getRecommendations(userId);
                if (recommendations != null) {
                    break;
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            if (recommendations == null) {
                return new PublicResponseDTO<>(uuidSupplier.get(), 500, "Failed to fetch recommendations", null);
            }

            return new PublicResponseDTO<>(uuidSupplier.get(), 200, "Recommendations fetched successfully", recommendations);
        } catch (UserNotFoundException e) {
            return new PublicResponseDTO<>(uuidSupplier.get(), 404, e.getMessage(), null);
        }
    }
}
