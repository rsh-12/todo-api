package ru.example.todo.facade.impl;
/*
 * Date: 6/5/21
 * Time: 10:47 PM
 * */

import org.springframework.stereotype.Component;
import ru.example.todo.exception.CustomException;
import ru.example.todo.facade.PasswordFacade;
import ru.example.todo.messaging.MessagingClient;
import ru.example.todo.messaging.request.TokenRequest;
import ru.example.todo.service.UserService;

@Component
public class PasswordFacadeImpl implements PasswordFacade {

    private final MessagingClient messagingService;
    private final UserService userService;

    public PasswordFacadeImpl(MessagingClient messagingService, UserService userService) {
        this.messagingService = messagingService;
        this.userService = userService;
    }

    @Override
    public void updatePassword(TokenRequest token, String password) {

        String email = messagingService.sendTokenAndReceiveEmail(token);
        if (email == null || email.isBlank()) {
            throw CustomException.internalServerError("An error occurred while generating the token");
        }
        userService.updatePassword(email, password);
    }
}
