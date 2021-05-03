package ru.example.todo.service.impl;
/*
 * Date: 3/25/21
 * Time: 4:39 PM
 * */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.example.todo.config.properties.TokenProperties;
import ru.example.todo.dto.UserDto;
import ru.example.todo.entity.RefreshToken;
import ru.example.todo.entity.Role;
import ru.example.todo.entity.User;
import ru.example.todo.exception.CustomException;
import ru.example.todo.repository.UserRepository;
import ru.example.todo.security.UserDetailsImpl;
import ru.example.todo.service.JwtTokenService;
import ru.example.todo.service.UserService;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private final AuthenticationManager authManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;
    private final TokenProperties tokenProperties;

    public UserServiceImpl(JwtTokenService jwtTokenService, UserRepository userRepository,
                           TokenProperties tokenProperties, AuthenticationManager authManager,
                           BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.jwtTokenService = jwtTokenService;
        this.userRepository = userRepository;
        this.tokenProperties = tokenProperties;
        this.authManager = authManager;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public String login(UserDto userDto) {

        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword()));

            User user = ((UserDetailsImpl) auth.getPrincipal()).getUser();

            return buildResponseBody(user);
        } catch (AuthenticationException ex) {
            throw new CustomException("Username not found or incorrect password", HttpStatus.NOT_FOUND);
        }
    }

    private String buildResponseBody(User user) {

        Map<String, String> body = new LinkedHashMap<>();

        String accessToken = jwtTokenService.buildAccessToken(user.getUsername(), user.getRoles());
        RefreshToken refreshToken = jwtTokenService.buildRefreshToken(user.getUsername());

        body.put("access_token", accessToken);
        body.put("refresh_token", refreshToken.getId());
        body.put("token_type", "Bearer");
        body.put("expires", String.valueOf(tokenProperties.getAccessTokenValidity()));

        try {
            return new ObjectMapper().writeValueAsString(body);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        throw new CustomException("Error while building response", HttpStatus.INTERNAL_SERVER_ERROR);
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
    public String refreshToken(String token) {
        RefreshToken oldRefreshToken = jwtTokenService.findRefreshToken(token);

        if (oldRefreshToken == null || !jwtTokenService.isValidRefreshToken(oldRefreshToken)) {
            throw new CustomException(
                    "Refresh token is not valid or expired, please, try to log in",
                    HttpStatus.BAD_REQUEST);
        }
        User user = userRepository.findByUsername(oldRefreshToken.getUsername())
                .orElseThrow(() -> new CustomException("Refresh token owner not found", HttpStatus.BAD_REQUEST));

        return buildResponseBody(user);
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new CustomException("User not found: " + userId, HttpStatus.NOT_FOUND);
        }
        userRepository.deleteById(userId);
    }

    @Override
    public User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("Username not found", HttpStatus.NOT_FOUND));
    }

}
