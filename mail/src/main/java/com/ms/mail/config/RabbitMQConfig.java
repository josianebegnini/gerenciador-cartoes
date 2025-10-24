package com.ms.mail.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String NORMAL_QUEUE = "email-normal-queue";
    public static final String HIGH_PRIORITY_QUEUE = "email-alta-prioridade-queue";
    public static final String DLQ = "email-dlq";
    public static final String EXCHANGE = "email-exchange";

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public TopicExchange emailExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue normalQueue() {
        return QueueBuilder.durable(NORMAL_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", DLQ)
                .build();
    }

    @Bean
    public Queue highPriorityQueue() {
        return QueueBuilder.durable(HIGH_PRIORITY_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", DLQ)
                .build();
    }

    @Bean
    public Queue dlq() {
        return QueueBuilder.durable(DLQ).build();
    }

    @Bean
    public Binding normalBinding() {
        return BindingBuilder.bind(normalQueue())
                .to(emailExchange())
                .with("email.normal");
    }

    @Bean
    public Binding highPriorityBinding() {
        return BindingBuilder.bind(highPriorityQueue())
                .to(emailExchange())
                .with("email.alta");
    }
}