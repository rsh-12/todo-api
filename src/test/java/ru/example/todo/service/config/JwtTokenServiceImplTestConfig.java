package ru.example.todo.service.config;
/*
 * Date: 5/9/21
 * Time: 9:35 AM
 * */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.example.todo.config.properties.TokenProperties;
import ru.example.todo.security.UserDetailsServiceImpl;
import ru.example.todo.service.JwtTokenService;
import ru.example.todo.service.TokenStore;
import ru.example.todo.service.impl.JwtTokenServiceImpl;

@TestConfiguration
public class JwtTokenServiceImplTestConfig {

    @TestConfiguration
    static class JwtTokenServiceImplConfig {

        @Autowired
        private UserDetailsServiceImpl uds;

        @Autowired
        private TokenProperties tokenProperties;

        @Autowired
        private TokenStore tokenStore;

        @Bean
        public JwtTokenService jwtTokenService() {
            return new JwtTokenServiceImpl(tokenProperties, uds, tokenStore);
        }
    }

}
