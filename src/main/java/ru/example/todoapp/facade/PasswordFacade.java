package ru.example.todoapp.facade;

import ru.example.todoapp.messaging.request.TokenRequest;

public interface PasswordFacade {

    void updatePassword(TokenRequest token, String password);
}
