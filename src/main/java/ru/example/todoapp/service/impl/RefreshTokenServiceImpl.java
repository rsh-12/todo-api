package ru.example.todoapp.service.impl;
/*
 * Date: 6/29/21
 * Time: 12:54 AM
 * */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.example.todoapp.entity.RefreshToken;
import ru.example.todoapp.repository.RefreshTokenRepository;
import ru.example.todoapp.service.JwtTokenService;
import ru.example.todoapp.service.RefreshTokenService;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenService jwtTokenService;

    @Autowired
    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, JwtTokenService jwtTokenService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public RefreshToken create(Long userId, String ip) {
        String token = jwtTokenService.buildRefreshToken();

        RefreshToken refresh = findByUserId(userId).orElseGet(RefreshToken::new);
        refresh.setToken(token);
        refresh.setUserId(userId);
        refresh.setCreatedByIp(ip);

        long expTime = jwtTokenService.getExpiration(token).getTime();
        refresh.setExpiresAt(new Timestamp(expTime).toLocalDateTime());

        CompletableFuture.runAsync(() -> refreshTokenRepository.save(refresh));

        return refresh;
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findOne(String token) {
        return refreshTokenRepository.findByToken(token)
                .filter(refreshToken -> jwtTokenService.isTokenValid(refreshToken.getToken()));
    }

    private Optional<RefreshToken> findByUserId(Long userId) {
        return refreshTokenRepository.findByUserId(userId);
    }

}
