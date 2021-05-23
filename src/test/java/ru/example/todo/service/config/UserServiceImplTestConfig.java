package ru.example.todo.service.config;
/*
 * Date: 5/10/21
 * Time: 7:07 PM
 * */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.example.todo.config.properties.TokenProperties;
import ru.example.todo.repository.UserRepository;
import ru.example.todo.service.JwtTokenService;
import ru.example.todo.service.UserService;
import ru.example.todo.service.impl.UserServiceImpl;

@TestConfiguration
public class UserServiceImplTestConfig {

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenProperties tokenProperties;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Bean
    public UserService userService() {
        return new UserServiceImpl(jwtTokenService, userRepository,
                tokenProperties, authenticationManager, bCryptPasswordEncoder);
    }

}
