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
import ru.example.todo.repository.UserRepository;
import ru.example.todo.service.EmailService;
import ru.example.todo.service.OtpService;
import ru.example.todo.util.GenerateCodeUtil;

import java.util.Date;
import java.util.Optional;

@Service
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public OtpServiceImpl(OtpRepository otpRepository, UserRepository userRepository, EmailService emailService) {
        this.otpRepository = otpRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Override
    public void sendOtp(JsonNode body) {
        Otp otp = renewOtp(body);
        save(otp);
        emailService.send(otp.getUsername(), otp.getCode());
    }

    @Override
    public boolean checkOtp(String username, String code) throws CustomException {
        Otp otp = otpRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("Not Found", "Username Not Found", HttpStatus.NOT_FOUND));

        if (!otp.getCode().equals(code) || isExpired(otp)) {
            throw new CustomException("Not Found", "Code not found or is expired", HttpStatus.NOT_FOUND);
        }
        return true;
    }

    private boolean isExpired(Otp otp) {
        return otp.getExpiresAt().before(new Date());
    }

    private Otp renewOtp(JsonNode body) {

        if (body == null || body.get("email") == null) {
            throw new CustomException("Bad Request", "Email is required", HttpStatus.BAD_REQUEST);
        }

        String email = body.get("email").asText();
        Optional<Otp> otpFromDb = otpRepository.findByUsername(email);

        return otpFromDb.map(this::updateOtp).orElseGet(() -> createOtp(new Otp(), email));
    }

    private Otp createOtp(Otp otp, String email) {
        checkUsername(email);

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

    private boolean checkUsername(String email) throws CustomException {
        boolean existsByUsername = userRepository.existsByUsername(email);
        if (!existsByUsername) {
            throw new CustomException("Not Found", "Username Not Found", HttpStatus.NOT_FOUND);
        }
        return existsByUsername;
    }
}
