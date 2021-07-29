package ru.example.todo.messaging;

import ru.example.todo.messaging.requests.EmailRequest;
import ru.example.todo.messaging.requests.TokenRequest;

public interface MessagingClient {

    void send(EmailRequest email);

    String sendTokenAndReceiveEmail(TokenRequest token);

}
