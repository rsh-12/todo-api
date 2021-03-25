package ru.example.todo.service.impl;
/*
 * Date: 3/25/21
 * Time: 4:39 PM
 * */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.example.todo.entity.User;
import ru.example.todo.exception.CustomException;
import ru.example.todo.repository.UserRepository;
import ru.example.todo.service.JwtTokenService;
import ru.example.todo.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private AuthenticationManager authManager;

    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;

    public UserServiceImpl(JwtTokenService jwtTokenService, UserRepository userRepository) {
        this.jwtTokenService = jwtTokenService;
        this.userRepository = userRepository;
    }


    @Override
    public String login(String username, String password) {
        try {

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            return jwtTokenService.buildAccessToken(username, user.getRoles());
        } catch (AuthenticationException ex) {
            throw new CustomException("Invalid username/password");
        }
    }

    @Override
    public Map<String, String> register(String username, String password) {
        return null;
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
