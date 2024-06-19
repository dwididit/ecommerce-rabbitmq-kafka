package dev.dwidi.ecommercerabbitmqkafka.dto.performance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceMetricsDTO {
    private Long messageCount;
    private Long duration;
    private Double messagesPerSecond;
}
