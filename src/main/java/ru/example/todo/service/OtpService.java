package ru.example.todo.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface OtpService {

    void sendOtp(JsonNode body);

}
