package ru.example.todo.facade;

import ru.example.todo.messaging.request.TokenRequest;

public interface PasswordFacade {

    void updatePassword(TokenRequest token, String password);
}
