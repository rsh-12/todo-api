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
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
}
