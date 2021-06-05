package ru.example.todo.service.impl;
/*
 * Date: 3/25/21
 * Time: 4:39 PM
 * */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.example.todo.config.properties.TokenProperties;
import ru.example.todo.domain.RefreshToken;
import ru.example.todo.dto.UserDto;
import ru.example.todo.entity.User;
import ru.example.todo.exception.CustomException;
import ru.example.todo.messaging.MessagingService;
import ru.example.todo.messaging.requests.TokenRequest;
import ru.example.todo.repository.UserRepository;
import ru.example.todo.security.UserDetailsImpl;
import ru.example.todo.service.JwtTokenService;
import ru.example.todo.service.UserService;

@Service
public class UserServiceImpl extends AbstractServiceClass implements UserService {

    @Autowired
    private MessagingService messagingService;

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

            return buildResponseBody(user, jwtTokenService, tokenProperties);
        } catch (AuthenticationException ex) {
            throw new CustomException("Not Found", "Username Not Found / Incorrect Password", HttpStatus.NOT_FOUND);
        }
    }


    @Override
    public String register(User user) {

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new CustomException("Bad Request", "Username already in use", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "ok";
    }

    @Override
    public String generateNewTokens(String refreshToken) {
        RefreshToken oldRefreshToken = jwtTokenService.findRefreshToken(refreshToken);

        if (oldRefreshToken == null || !jwtTokenService.hasRefreshTokenExpired(oldRefreshToken)) {
            throw new CustomException(
                    "Refresh token is not valid or expired, please, try to log in",
                    HttpStatus.BAD_REQUEST);
        }
        User user = userRepository.findByUsername(oldRefreshToken.getUsername())
                .orElseThrow(() -> new CustomException("Not Found", "Refresh token owner not found", HttpStatus.BAD_REQUEST));

        return buildResponseBody(user, jwtTokenService, tokenProperties);
    }

    @Override
    public void deleteUserById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new CustomException("Not Found", "User Not Found: " + userId, HttpStatus.NOT_FOUND);
        }
        userRepository.deleteById(userId);
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("Not Found", "Username not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("Not Found", "User Not Found", HttpStatus.BAD_REQUEST));
    }

    @Override
    public void updatePassword(User user, String password) {

        if (password == null || password.isBlank() || password.length() < 8) {
            throw new CustomException("Bad Request", "Invalid data", HttpStatus.BAD_REQUEST);
        }

        String encodedPassword = bCryptPasswordEncoder.encode(password);
        user.setPassword(encodedPassword);
    }

    @Override
    public void updatePassword(TokenRequest token, String password) {
        String email = messagingService.sendTokenAndReceiveEmail(token);
        if (email == null || email.isBlank()) {
            throw new CustomException("Internal Server Error", "An error occurred while generating the token",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        User user = userRepository.findByUsername(email)
                .orElseThrow(() -> new CustomException("Not Found", "Username Not Found", HttpStatus.BAD_REQUEST));

        user.setPassword(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);
    }

    @Override
    public boolean existsByUsername(String email) {
        return userRepository.existsByUsername(email);
    }

}
