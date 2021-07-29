package ru.example.todo.messaging;

import ru.example.todo.messaging.request.EmailRequest;
import ru.example.todo.messaging.request.TokenRequest;

public interface MessagingClient {

    void send(EmailRequest email);

    String sendTokenAndReceiveEmail(TokenRequest token);

}
