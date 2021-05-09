package ru.example.todo.service;
/*
 * Date: 5/9/21
 * Time: 9:01 AM
 * */

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import ru.example.todo.exception.CustomException;
import ru.example.todo.service.config.JwtTokenServiceImplTestConfig;

import static org.junit.Assert.assertThrows;

@Import(JwtTokenServiceImplTestConfig.class)
public class JwtTokenServiceTest extends AbstractServiceTestClass {

    @Qualifier("jwtTokenService")
    @Autowired
    private JwtTokenService tokenService;

    @Test
    public void validateToken_ShouldThrowException() throws CustomException {
        assertThrows(CustomException.class, () -> tokenService.validateToken("invalid"));
    }
}
