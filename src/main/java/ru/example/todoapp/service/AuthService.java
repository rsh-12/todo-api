package ru.example.todoapp.service;

import ru.example.todoapp.controller.request.CredentialsRequest;
import ru.example.todoapp.entity.User;

import java.util.Map;
import java.util.Optional;

public interface AuthService {

    Optional<Map<String, String>> login(CredentialsRequest credentials, String clientIp);

    User register(CredentialsRequest credentials);

    Optional<Map<String, String>> generateNewTokens(String token, String clientIp);

}
