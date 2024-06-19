package dev.dwidi.ecommercerabbitmqkafka.service.rabbitmq;

import dev.dwidi.ecommercerabbitmqkafka.dto.PublicResponseDTO;
import dev.dwidi.ecommercerabbitmqkafka.dto.performance.PerformanceMetricsDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
public class RabbitMQPerformanceTestService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String EXCHANGE_NAME = "performance-test-exchange";
    private static final String ROUTING_KEY = "performance-test-routingKey";

    public PublicResponseDTO<PerformanceMetricsDTO> sendMessages(int messageCount) {
        CountDownLatch latch = new CountDownLatch(messageCount);
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < messageCount; i++) {
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, "Test Message " + i);
            latch.countDown();
        }

        try {
            latch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        double messagesPerSecond = messageCount / (duration / 1000.0);

        PerformanceMetricsDTO metrics = new PerformanceMetricsDTO((long) messageCount, duration, messagesPerSecond);
        return new PublicResponseDTO<>(UUID.randomUUID().toString(), 200, "RabbitMQ test completed.", metrics);
    }
}
