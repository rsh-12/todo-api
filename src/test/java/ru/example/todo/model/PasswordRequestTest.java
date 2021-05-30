package ru.example.todo.model;
/*
 * Date: 5/20/21
 * Time: 12:01 PM
 * */

import org.junit.Before;
import org.junit.Test;
import ru.example.todo.domain.request.PasswordRequest;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.Assert.*;

public class PasswordRequestTest {

    private Validator validator;

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void createPasswordRequest_ShouldThrowValidationExceptions() {
        PasswordRequest passwordRequest = new PasswordRequest();

        passwordRequest.setUsername("invalid_username");
        passwordRequest.setPassword("123"); // min size = 8
        Set<ConstraintViolation<PasswordRequest>> violations = validator.validate(passwordRequest);
        assertFalse(violations.isEmpty());
        assertEquals(3, violations.size());
    }

    @Test
    public void createPasswordRequest_ShouldNotThrowAnyExceptions() {
        PasswordRequest passwordRequest = new PasswordRequest();

        passwordRequest.setUsername("some@bk.ru");
        passwordRequest.setPassword("12345678"); // min size = 8
        Set<ConstraintViolation<PasswordRequest>> violations = validator.validate(passwordRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void createPasswordRequestWithConstructor() {
        String username = "user@mail.com";
        String pwd = "password1234";

        PasswordRequest passwordRequest = new PasswordRequest(username, pwd);

        Set<ConstraintViolation<PasswordRequest>> violations = validator.validate(passwordRequest);
        assertTrue(violations.isEmpty());

        assertEquals(passwordRequest.getUsername(), username);
        assertEquals(passwordRequest.getPassword(), pwd);
    }
}
