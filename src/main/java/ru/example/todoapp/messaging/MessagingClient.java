package ru.example.todoapp.messaging;

import ru.example.todoapp.messaging.request.EmailRequest;
import ru.example.todoapp.messaging.request.TokenRequest;

public interface MessagingClient {

    void send(EmailRequest email);

    String sendTokenAndReceiveEmail(TokenRequest token);

}
