package ru.example.todo.messaging;

import ru.example.todo.messaging.requests.EmailRequest;
import ru.example.todo.messaging.requests.TokenRequest;

public interface MessagingService {

    void send(EmailRequest email);

    String send(TokenRequest token);

}
