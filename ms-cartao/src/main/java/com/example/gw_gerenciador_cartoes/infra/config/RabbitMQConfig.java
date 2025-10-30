package com.example.gw_gerenciador_cartoes.infra.config;

import com.example.gw_gerenciador_cartoes.application.dto.cartao.ClienteContaCriadoDTO;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    @Value("${broker.exchange.cartao}")
    private String cartaoExchange;

    @Value("${broker.queue.cartao-criar}")
    private String cartaoCriarQueue;

    @Value("${broker.routing-key.cartao-criar}")
    private String cartaoCriarRoutingKey;

    @Value("${broker.queue.cartao-dlq}")
    private String cartaoDlqQueue;

    @Bean
    public TopicExchange cartaoExchange() {
        return new TopicExchange(cartaoExchange);
    }

    @Bean
    public DirectExchange cartaoDlqExchange() {
        return new DirectExchange("cartao-dlq-exchange");

    }

    @Bean
    public Queue cartaoCriarQueue() {
        return QueueBuilder.durable(cartaoCriarQueue)
                .withArgument("x-dead-letter-exchange", "cartao-dlq-exchange")
                .withArgument("x-dead-letter-routing-key", cartaoDlqQueue)
                .build();
    }

    @Bean
    public Queue cartaoDlqQueue() {
        return QueueBuilder.durable(cartaoDlqQueue).build();
    }

    @Bean
    public Binding bindingCartaoCriar() {
        return BindingBuilder
                .bind(cartaoCriarQueue())
                .to(cartaoExchange())
                .with(cartaoCriarRoutingKey);
    }

    @Bean
    public Binding bindingCartaoDlq() {
        return BindingBuilder
                .bind(cartaoDlqQueue())
                .to(cartaoDlqExchange())
                .with(cartaoDlqQueue);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();

        DefaultClassMapper classMapper = new DefaultClassMapper();
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("com.example.gw_gerenciador_cartoes.infra.dto.ClienteContaCriadoDTO", ClienteContaCriadoDTO.class);
        classMapper.setIdClassMapping(idClassMapping);
        converter.setClassMapper(classMapper);

        return converter;
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
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }

}
