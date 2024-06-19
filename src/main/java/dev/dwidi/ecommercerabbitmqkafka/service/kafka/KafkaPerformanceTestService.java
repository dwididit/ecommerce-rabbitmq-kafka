package dev.dwidi.ecommercerabbitmqkafka.service;

import dev.dwidi.ecommercerabbitmqkafka.dto.PublicResponseDTO;
import dev.dwidi.ecommercerabbitmqkafka.dto.performance.PerformanceMetricsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
public class KafkaPerformanceTestService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC_NAME = "performance-test-topic";

    public PublicResponseDTO<PerformanceMetricsDTO> sendMessages(int messageCount) {
        CountDownLatch latch = new CountDownLatch(messageCount);
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < messageCount; i++) {
            ListenableFuture<SendResult<String, String>> future = adaptToListenableFuture(kafkaTemplate.send(TOPIC_NAME, "Test Message " + i));
            future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
                @Override
                public void onSuccess(SendResult<String, String> result) {
                    latch.countDown();
                }

                @Override
                public void onFailure(Throwable ex) {
                    latch.countDown();
                }
            });
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
        return new PublicResponseDTO<>(UUID.randomUUID().toString(), 200, "Kafka test completed.", metrics);
    }

    private ListenableFuture<SendResult<String, String>> adaptToListenableFuture(CompletableFuture<SendResult<String, String>> completableFuture) {
        SettableListenableFuture<SendResult<String, String>> listenableFuture = new SettableListenableFuture<>();
        completableFuture.whenComplete((result, ex) -> {
            if (ex != null) {
                listenableFuture.setException(ex);
            } else {
                listenableFuture.set(result);
            }
        });
        return listenableFuture;
    }
}
