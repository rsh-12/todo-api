package ru.example.todoapp.facade;

import ru.example.todoapp.controller.request.TokenRequest;

public interface PasswordFacade {

    void updatePassword(TokenRequest token, String password);
}
