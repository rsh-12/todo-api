package ru.example.todoapp.validation;
/*
 * Date: 27.08.2021
 * Time: 9:52 AM
 * */

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class CustomEmailValidator implements ConstraintValidator<ValidEmail, String> {

    private static final Pattern EMAIL = Pattern
            .compile("^[a-z_-]{2,}[0-9a-z_-]*@[a-z]{2,5}\\\\.(ru|com|list)",
                    Pattern.CASE_INSENSITIVE);

    @Override
    public boolean isValid(String s, ConstraintValidatorContext ctx) {
        if (s == null || s.isBlank()) {
            return false;
        }
        return EMAIL.matcher(s).matches();
    }
}
