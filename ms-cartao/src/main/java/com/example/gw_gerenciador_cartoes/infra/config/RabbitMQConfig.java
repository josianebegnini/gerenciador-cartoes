package com.example.gw_gerenciador_cartoes.infra.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${broker.exchange.cartao}")
    private String cartaoExchange;

    @Value("${broker.queue.cartao-criar-cartao}")
    private String cartaoCriarCartaoQueue;

    @Value("${broker.routing-key.cartao-criar-cartao}")
    private String cartaoCriarCartaoRoutingKey;

    @Bean
    public TopicExchange cartaoExchange() {
        return new TopicExchange(cartaoExchange);
    }

    @Bean
    public Queue cartaoCriarCartaoQueue() {
        return new Queue(cartaoCriarCartaoQueue, true);
    }

    @Bean
    public Binding bindingCartaoCriarCartao() {
        return BindingBuilder
                .bind(cartaoCriarCartaoQueue())
                .to(cartaoExchange())
                .with(cartaoCriarCartaoRoutingKey);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // Suporte a tipos como LocalDate, BigDecimal etc.
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        return factory;
    }

}
