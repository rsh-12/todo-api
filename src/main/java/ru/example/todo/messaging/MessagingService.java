package ru.example.todo.messaging;

import ru.example.todo.messaging.requests.Email;
import ru.example.todo.messaging.requests.Token;

public interface MessagingService {

    void send(Email email);

    String send(Token token);

}
