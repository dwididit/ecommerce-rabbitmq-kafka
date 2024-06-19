package dev.dwidi.ecommercerabbitmqkafka.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PerformanceRabbitMQConfig {

    public static final String PERFORMANCE_QUEUE_NAME = "performance_test_queue";
    public static final String PERFORMANCE_EXCHANGE_NAME = "performance_test_exchange";
    public static final String PERFORMANCE_ROUTING_KEY = "performance_test_routingKey";

    @Bean
    public Queue performanceQueue() {
        return new Queue(PERFORMANCE_QUEUE_NAME, true);
    }

    @Bean
    public DirectExchange performanceExchange() {
        return new DirectExchange(PERFORMANCE_EXCHANGE_NAME);
    }

    @Bean
    public Binding performanceBinding(Queue performanceQueue, DirectExchange performanceExchange) {
        return BindingBuilder.bind(performanceQueue).to(performanceExchange).with(PERFORMANCE_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter performanceJackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate performanceRabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(performanceJackson2JsonMessageConverter());
        return rabbitTemplate;
    }
}
