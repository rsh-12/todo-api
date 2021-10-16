package ru.example.todoapp.service.impl;
/*
 * Date: 3/25/21
 * Time: 4:39 PM
 * */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.example.todoapp.entity.User;
import ru.example.todoapp.exception.CustomException;
import ru.example.todoapp.repository.UserRepository;
import ru.example.todoapp.service.UserService;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public void delete(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw CustomException.notFound("User not found: id=" + userId);
        }
        userRepository.deleteById(userId);
    }

    @Override
    public Optional<User> findOne(Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public Optional<User> updatePassword(String email, String password) {
        return userRepository.findByUsername(email)
                .map(user -> {
                    user.setPassword(bCryptPasswordEncoder.encode(password));
                    return userRepository.save(user);
                });
    }

    @Override
    public boolean existsByUsername(String email) {
        return userRepository.existsByUsername(email);
    }

}
