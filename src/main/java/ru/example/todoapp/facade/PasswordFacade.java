package ru.example.todoapp.facade;

import ru.example.todoapp.domain.request.TokenRequest;

public interface PasswordFacade {

    void updatePassword(TokenRequest token, String password);
}
