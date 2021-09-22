package ru.example.todoapp.messaging;

import ru.example.todoapp.domain.request.EmailRequest;
import ru.example.todoapp.domain.request.TokenRequest;

public interface MessagingClient {

    void send(EmailRequest email);

    String sendTokenAndReceiveEmail(TokenRequest token);

}
