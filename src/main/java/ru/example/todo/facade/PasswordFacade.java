package ru.example.todo.facade;

import ru.example.todo.messaging.requests.TokenRequest;

public interface PasswordFacade {

    void updatePassword(TokenRequest token, String password);
}
