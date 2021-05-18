package ru.example.todo.service.impl;
/*
 * Date: 5/18/21
 * Time: 3:21 PM
 * */

import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.example.todo.service.EmailService;

import java.util.Objects;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final Environment env;

    public EmailServiceImpl(JavaMailSender mailSender, Environment env) {
        this.mailSender = mailSender;
        this.env = env;
    }

    @Async
    @Override
    public void send(String email, String code) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);
        message.setSubject("There is your secret code!");
        message.setText("Don't show it to anyone! Code = " + code);
        message.setFrom(Objects.requireNonNull(env.getProperty("MAIL_USER")));

        mailSender.send(message);
    }
}
