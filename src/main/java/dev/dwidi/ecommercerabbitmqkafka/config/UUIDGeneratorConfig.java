package dev.dwidi.ecommercerabbitmqkafka.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;
import java.util.function.Supplier;

@Configuration
public class UUIDGeneratorConfig {

    @Bean
    public Supplier<String> uuidSupplier() {
        return () -> UUID.randomUUID().toString();
    }
}
