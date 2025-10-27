package com.example.gw_gerenciador_cartoes.infra.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "broker.exchange.cartao";

    public static final String QUEUE_CRIAR_CARTAO = "broker.queue.cartao-criar-cartao";
    public static final String ROUTING_KEY_CRIAR_CARTAO = "broker.routing-key.cartao-criar-cartao";

    public static final String QUEUE_EMAIL_NORMAL_QUEUE = "broker.queue.email-normal-queue";
    public static final String ROUTING_KEY_EMAIL_NORMAL_QUEUE = "broker.routing-key.email-normal-queue";


    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue queueCriarCartao() {
        return new Queue(QUEUE_CRIAR_CARTAO, true);
    }

    @Bean
    public Binding bindingCriarCartao(Queue queueCriarCartao, DirectExchange exchange) {
        return BindingBuilder.bind(queueCriarCartao).to(exchange).with(ROUTING_KEY_CRIAR_CARTAO);
    }

    @Bean
    public Queue queueEmailNormalQueue() {
        return new Queue(QUEUE_EMAIL_NORMAL_QUEUE, true);
    }

    @Bean
    public Binding bindingSendEmail(Queue queueEmailNormalQueue, DirectExchange exchange) {
        return BindingBuilder.bind(queueEmailNormalQueue).to(exchange).with(ROUTING_KEY_EMAIL_NORMAL_QUEUE);
    }

}