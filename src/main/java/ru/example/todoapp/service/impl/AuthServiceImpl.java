package ru.example.todoapp.service.impl;
/*
 * Date: 16.08.2021
 * Time: 2:42 PM
 * */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.example.todoapp.config.properties.TokenProperties;
import ru.example.todoapp.controller.request.CredentialsRequest;
import ru.example.todoapp.entity.RefreshToken;
import ru.example.todoapp.entity.User;
import ru.example.todoapp.exception.CustomException;
import ru.example.todoapp.repository.UserRepository;
import ru.example.todoapp.security.UserDetailsImpl;
import ru.example.todoapp.service.AuthService;
import ru.example.todoapp.service.JwtTokenService;
import ru.example.todoapp.service.RefreshTokenService;

import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;
    private final TokenProperties tokenProperties;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenService jwtTokenService;

    @Autowired
    public AuthServiceImpl(AuthenticationManager authManager, BCryptPasswordEncoder bCryptPasswordEncoder,
                           UserRepository userRepository, TokenProperties tokenProperties,
                           RefreshTokenService refreshTokenService, JwtTokenService jwtTokenService) {
        this.authManager = authManager;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
        this.tokenProperties = tokenProperties;
        this.refreshTokenService = refreshTokenService;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public Map<String, String> login(CredentialsRequest credentials, String ip) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(credentials.username(), credentials.password()));
            User userFromDb = ((UserDetailsImpl) auth.getPrincipal()).getUser();
            return buildResponseBody(userFromDb, ip);
        } catch (AuthenticationException ex) {
            throw CustomException.notFound("Username not found/Incorrect password");
        }
    }

    @Override
    public User register(CredentialsRequest credentials) {
        if (userRepository.existsByUsername(credentials.username())) {
            throw CustomException.badRequest("Username already in use");
        }
        User user = new User();
        user.setUsername(credentials.username());
        user.setPassword(bCryptPasswordEncoder.encode(credentials.password()));

        return userRepository.save(user);
    }

    @Override
    public Map<String, String> generateNewTokens(String refreshToken, String ip) {
        RefreshToken oldRefreshToken = refreshTokenService.findRefreshTokenByValue(refreshToken);
        User user = userRepository.findById(oldRefreshToken.getUserId())
                .orElseThrow(() -> CustomException.notFound("Refresh token owner not found"));

        return buildResponseBody(user, ip); // generate new access and refresh tokens
    }

    Map<String, String> buildResponseBody(User user, String ip) {
        String accessToken = jwtTokenService.buildAccessToken(user.getId(), user.getRoles());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId(), ip);

        return Map.of("access_token", accessToken,
                "expires_in", String.valueOf(tokenProperties.getAccessTokenValidity()),
                "refresh_token", refreshToken.getToken(),
                "refresh_expires_in", String.valueOf(tokenProperties.getRefreshTokenValidity()),
                "token_type", "Bearer");
    }

}
