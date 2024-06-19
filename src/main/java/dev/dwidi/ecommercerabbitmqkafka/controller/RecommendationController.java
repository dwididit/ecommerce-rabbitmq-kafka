package dev.dwidi.ecommercerabbitmqkafka.service;

import dev.dwidi.ecommercerabbitmqkafka.dto.PublicResponseDTO;
import dev.dwidi.ecommercerabbitmqkafka.entity.Product;
import dev.dwidi.ecommercerabbitmqkafka.exception.UserNotFoundException;
import dev.dwidi.ecommercerabbitmqkafka.service.kafka.KafkaConsumerService;
import dev.dwidi.ecommercerabbitmqkafka.service.kafka.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private KafkaConsumerService kafkaConsumerService;

    @PostMapping("/byUser")
    public PublicResponseDTO<List<Product>> recommendProducts(@RequestParam Long userId) {
        try {
            // Check if the user exists before sending the Kafka message
            kafkaConsumerService.checkUserExists(userId);

            // User exists, proceed to send Kafka message
            kafkaProducerService.sendMessage(userId);

            // Wait for Kafka to process the message and produce recommendations
            List<Product> recommendations = null;
            for (int i = 0; i < 5; i++) { // Reduce the number of retries and wait time
                recommendations = kafkaConsumerService.getRecommendations(userId);
                if (recommendations != null) {
                    break;
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(500); // Use shorter wait time
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            if (recommendations == null) {
                return new PublicResponseDTO<>(UUID.randomUUID().toString(), 500, "Failed to fetch recommendations", null);
            }

            return new PublicResponseDTO<>(UUID.randomUUID().toString(), 200, "Recommendations fetched successfully", recommendations);
        } catch (UserNotFoundException e) {
            return new PublicResponseDTO<>(UUID.randomUUID().toString(), 404, e.getMessage(), null);
        }
    }
}