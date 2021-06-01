package ru.example.todo.messaging;
/*
 * Date: 5/23/21
 * Time: 11:26 AM
 * */

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.example.todo.exception.CustomException;
import ru.example.todo.messaging.requests.Email;
import ru.example.todo.messaging.requests.Token;
import ru.example.todo.service.UserService;

@Service
public class MessagingClient implements MessagingService {

    private final RabbitTemplate rabbitTemplate;
    private final DirectExchange emailExchange;
    private final DirectExchange tokenExchange;
    private final UserService userService;

    @Autowired
    public MessagingClient(RabbitTemplate rabbitTemplate, DirectExchange emailExchange,
                           DirectExchange tokenExchange, UserService userService) {
        this.rabbitTemplate = rabbitTemplate;
        this.emailExchange = emailExchange;
        this.tokenExchange = tokenExchange;
        this.userService = userService;
    }

    @Override
    public void send(Email email) {
        if (!userService.existsByUsername(email.getEmail())) {
            throw new CustomException("Not Found", "Username Not Found", HttpStatus.NOT_FOUND);
        }

        Object response = getResponse(emailExchange, email, "todo.email.replies");
        if (response == null || ((boolean) response == false)) {
            throw new CustomException("Internal Server Error", "An error occurred while generating the token",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String send(Token token) {
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
