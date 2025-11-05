package com.example.gw_gerenciador_cartoes.infra.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_CARTAO = "cartao.exchange";
    public static final String EXCHANGE_EMAIL = "email-exchange";

    public static final String ROUTING_KEY_CRIAR_CARTAO = "cartao.criar";

     public static final String ROUTING_KEY_EMAIL_NORMAL_QUEUE = "email.normal";

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        return new Jackson2JsonMessageConverter(objectMapper);
    }

}