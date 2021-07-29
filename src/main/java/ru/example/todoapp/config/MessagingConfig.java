package ru.example.todoapp.config;
/*
 * Date: 5/23/21
 * Time: 11:25 AM
 * */

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingConfig {

    @Bean
    public DirectExchange emailExchange() {
        return new DirectExchange("todo.email.exchange");
    }

    @Bean
    public DirectExchange tokenExchange() {
        return new DirectExchange("todo.token.exchange");
    }

    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

}
