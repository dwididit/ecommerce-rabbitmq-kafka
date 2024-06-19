package dev.dwidi.ecommercerabbitmqkafka.controller;

import dev.dwidi.ecommercerabbitmqkafka.dto.PublicResponseDTO;
import dev.dwidi.ecommercerabbitmqkafka.dto.performance.PerformanceMetricsDTO;
import dev.dwidi.ecommercerabbitmqkafka.service.KafkaPerformanceTestService;
import dev.dwidi.ecommercerabbitmqkafka.service.rabbitmq.RabbitMQPerformanceTestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PerformanceTestController {

    @Autowired
    private RabbitMQPerformanceTestService rabbitMQPerformanceTestService;

    @Autowired
    private KafkaPerformanceTestService kafkaPerformanceTestService;

    @Operation(summary = "Test RabbitMQ performance", description = "Send a specified number of messages to RabbitMQ and measure performance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "RabbitMQ test completed successfully"),
            @ApiResponse(responseCode = "403", description = "Access forbidden")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/test/rabbitmq")
    public PublicResponseDTO<PerformanceMetricsDTO> testRabbitMQ(
            @Parameter(description = "Number of messages to send") @RequestParam int messageCount) {
        return rabbitMQPerformanceTestService.sendMessages(messageCount);
    }

    @Operation(summary = "Test Kafka performance", description = "Send a specified number of messages to Kafka and measure performance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kafka test completed successfully"),
            @ApiResponse(responseCode = "403", description = "Access forbidden")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/test/kafka")
    public PublicResponseDTO<PerformanceMetricsDTO> testKafka(
            @Parameter(description = "Number of messages to send") @RequestParam int messageCount) {
        return kafkaPerformanceTestService.sendMessages(messageCount);
    }
}
