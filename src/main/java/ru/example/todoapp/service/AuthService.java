package ru.example.todoapp.service;

import ru.example.todoapp.controller.request.CredentialsRequest;
import ru.example.todoapp.entity.User;

import java.util.Map;

public interface AuthService {

    Map<String, String> login(CredentialsRequest credentials, String clientIp);

    User register(CredentialsRequest credentials);

    Map<String, String> generateNewTokens(String token, String clientIp);

}
