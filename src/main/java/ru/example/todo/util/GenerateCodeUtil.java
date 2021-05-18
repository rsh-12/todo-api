package ru.example.todo.util;
/*
 * Date: 5/18/21
 * Time: 7:11 PM
 * */

import ru.example.todo.exception.CustomException;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public final class GenerateCodeUtil {

    private GenerateCodeUtil() {

    }

    public static String generateCode() {
        String code;

        try {
            SecureRandom random = SecureRandom.getInstanceStrong();
            int otp = random.nextInt(900_000) + 100_000; // 100 000 - 999 999 (6-digit)
            code = String.valueOf(otp);
        } catch (NoSuchAlgorithmException e) {
            throw new CustomException("Error during code generation");
        }

        return code;
    }
}
