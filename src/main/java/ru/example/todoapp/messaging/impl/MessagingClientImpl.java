package ru.example.todoapp.messaging.impl;
/*
 * Date: 5/23/21
 * Time: 11:26 AM
 * */

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.example.todoapp.domain.request.EmailRequest;
import ru.example.todoapp.domain.request.TokenRequest;
import ru.example.todoapp.exception.CustomException;
import ru.example.todoapp.exception.NotFoundException;
import ru.example.todoapp.messaging.MessagingClient;
import ru.example.todoapp.service.UserService;

@Service
public class MessagingClientImpl implements MessagingClient {

    private final RabbitTemplate rabbitTemplate;
    private final DirectExchange emailExchange;
    private final DirectExchange tokenExchange;
    private final UserService userService;

    @Autowired
    public MessagingClientImpl(RabbitTemplate rabbitTemplate, DirectExchange emailExchange,
            DirectExchange tokenExchange, UserService userService) {
        this.rabbitTemplate = rabbitTemplate;
        this.emailExchange = emailExchange;
        this.tokenExchange = tokenExchange;
        this.userService = userService;
    }

    @Override
    public void send(EmailRequest emailRequest) {
        if (!userService.existsByUsername(emailRequest.getEmail())) {
            throw new NotFoundException("Username not found: email=" + emailRequest);
        }

        Object response = getResponse(emailExchange, emailRequest, "todo.email.replies");
        if (response == null || ((boolean) response == false)) {
            throw CustomException.createInternalServerErrorExc(
                    "An error occurred while generating the token");
        }
    }

    @Override
    public String sendTokenAndReceiveEmail(TokenRequest token) {
        Object response = getResponse(tokenExchange, token, "todo.token.replies");
        return response == null ? "" : (String) response;
    }

    private <T> Object getResponse(DirectExchange exchange, T data, String replyTo) {
        return rabbitTemplate
                .convertSendAndReceive(exchange.getName(), "todo", data, message -> {
                    MessageProperties properties = message.getMessageProperties();
                    properties.setReplyTo(replyTo);
                    return message;
                });
    }

}
