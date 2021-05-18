package ru.example.todo.service.impl;
/*
 * Date: 5/18/21
 * Time: 4:21 PM
 * */

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.example.todo.entity.Otp;
import ru.example.todo.exception.CustomException;
import ru.example.todo.repository.OtpRepository;
import ru.example.todo.service.EmailService;
import ru.example.todo.service.OtpService;
import ru.example.todo.util.GenerateCodeUtil;

import java.util.Optional;

@Service
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final EmailService emailService;

    public OtpServiceImpl(OtpRepository otpRepository, EmailService emailService) {
        this.otpRepository = otpRepository;
        this.emailService = emailService;
    }


    @Override
    public void sendOtp(JsonNode body) {
        Otp otp = renewOtp(body);
        save(otp);
        emailService.send(otp.getUsername(), otp.getCode());
    }

    private Otp renewOtp(JsonNode body) {

        if (body == null || body.get("email") == null) {
            throw new CustomException("Bad Request", "Email is required!", HttpStatus.BAD_REQUEST);
        }

        String email = body.get("email").asText();
        Optional<Otp> otpFromDb = otpRepository.findByUsername(email);

        return otpFromDb.map(this::updateOtp).orElseGet(() -> createOtp(new Otp(), email));
    }

    private Otp createOtp(Otp otp, String email) {
        otp.setCode(GenerateCodeUtil.generateCode());
        otp.setUsername(email);
        return otp;
    }

    private Otp updateOtp(Otp otp) {
        otp.setCode(GenerateCodeUtil.generateCode());
        otp.updateExpiryDate();
        return otp;
    }

    private void save(Otp otp) {
        otpRepository.save(otp);
    }
}
