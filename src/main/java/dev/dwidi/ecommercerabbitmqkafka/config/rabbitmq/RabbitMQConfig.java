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
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "user_crud_queue";
    public static final String EXCHANGE_NAME = "user_crud_exchange";
    public static final String ROUTING_KEY = "user_crud_routingKey";

    public static final String PERFORMANCE_TEST_QUEUE_NAME = "performance-test-queue";
    public static final String PERFORMANCE_TEST_EXCHANGE_NAME = "performance-test-exchange";
    public static final String PERFORMANCE_TEST_ROUTING_KEY = "performance-test-routingKey";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Queue performanceTestQueue() {
        return new Queue(PERFORMANCE_TEST_QUEUE_NAME, true);
    }

    @Bean
    public DirectExchange performanceTestExchange() {
        return new DirectExchange(PERFORMANCE_TEST_EXCHANGE_NAME);
    }

    @Bean
    public Binding performanceTestBinding(Queue performanceTestQueue, DirectExchange performanceTestExchange) {
        return BindingBuilder.bind(performanceTestQueue).to(performanceTestExchange).with(PERFORMANCE_TEST_ROUTING_KEY);
    }
}
