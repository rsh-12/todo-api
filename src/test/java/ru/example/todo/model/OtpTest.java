package ru.example.todo.model;
/*
 * Date: 5/21/21
 * Time: 9:45 AM
 * */

import org.junit.Before;
import org.junit.Test;
import ru.example.todo.entity.Otp;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Date;
import java.util.Set;

import static org.junit.Assert.*;

public class OtpTest {

    private Validator validator;

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void createOtp_ShouldThrowValidationExceptions() {
        Otp otp = new Otp();
        otp.setUsername("");
        otp.setCode("1");

        Set<ConstraintViolation<Otp>> violations = validator.validate(otp);
        System.out.println(violations);

        assertFalse(violations.isEmpty());
    }

    @Test
    public void createOtp_ShouldCreateOtp() {
        String username = "user@mail.com", code = "123456";
        Otp otp = new Otp(username, code);

        Set<ConstraintViolation<Otp>> violations = validator.validate(otp);
        assertTrue(violations.isEmpty());

        assertTrue(otp.getExpiresAt().after(new Date()));
        assertEquals(username, otp.getUsername());
        assertEquals(code, otp.getCode());
    }

}
