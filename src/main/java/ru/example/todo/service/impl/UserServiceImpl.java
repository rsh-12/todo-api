package ru.example.todo.service.impl;
/*
 * Date: 3/25/21
 * Time: 4:39 PM
 * */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.example.todo.config.properties.TokenProperties;
import ru.example.todo.dto.UserDto;
import ru.example.todo.entity.RefreshToken;
import ru.example.todo.entity.Role;
import ru.example.todo.entity.User;
import ru.example.todo.exception.CustomException;
import ru.example.todo.repository.UserRepository;
import ru.example.todo.service.JwtTokenService;
import ru.example.todo.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;
    private final TokenProperties tokenProperties;

    public UserServiceImpl(JwtTokenService jwtTokenService, UserRepository userRepository, TokenProperties tokenProperties) {
        this.jwtTokenService = jwtTokenService;
        this.userRepository = userRepository;
        this.tokenProperties = tokenProperties;
    }

    @Override
    public String login(UserDto userDto) {
        try {
            User user = userRepository.findByUsername(userDto.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            authManager.authenticate(new UsernamePasswordAuthenticationToken(
                    userDto.getUsername(), userDto.getPassword()));

            return buildResponseBody(user);
        } catch (AuthenticationException | JsonProcessingException ex) {
            throw new CustomException("Invalid username/password", HttpStatus.BAD_REQUEST);
        }
    }

    private String buildResponseBody(User user) throws JsonProcessingException {

        Map<String, String> body = new LinkedHashMap<>();

        String accessToken = jwtTokenService.buildAccessToken(user.getUsername(), user.getRoles());
        RefreshToken refreshToken = jwtTokenService.buildRefreshToken(user.getUsername());

        body.put("access_token", accessToken);
        body.put("access_token_exp", String.valueOf(tokenProperties.getAccessTokenValidity()));
        body.put("refresh_token", refreshToken.getId());
        body.put("refresh_token_exp", String.valueOf(tokenProperties.getRefreshTokenValidity()));

        return new ObjectMapper().writeValueAsString(body);
    }

    @Override
    public String register(User user) {

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new CustomException("Username already in use", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singleton(Role.ROLE_USER));

        userRepository.save(user);
        return "ok";
    }

    @Override
    public Map<String, String> refreshToken(String token) {
        return null;
    }

    @Override
    public String deleteUser(String username) {
        return null;
    }

    @Override
    public User getUser(String username) {
        return null;
    }

    @Override
    public User whoAmI(HttpServletRequest request) {
        return null;
    }
}
