package ru.example.todoapp.messaging;

import ru.example.todoapp.controller.request.EmailRequest;
import ru.example.todoapp.controller.request.TokenRequest;

public interface MessagingClient {

    void send(EmailRequest email);

    String sendTokenAndReceiveEmail(TokenRequest token);

}
