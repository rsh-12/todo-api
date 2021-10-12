package ru.example.todoapp.service;
/*
 * Date: 7/3/21
 * Time: 1:41 PM
 * */

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.example.todoapp.config.properties.TokenProperties;
import ru.example.todoapp.entity.RefreshToken;
import ru.example.todoapp.repository.RefreshTokenRepository;
import ru.example.todoapp.service.impl.RefreshTokenServiceImpl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
public class RefreshTokenServiceTest {

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private TokenProperties tokenProperties;

    @Mock
    private JwtTokenService jwtTokenService;

    @BeforeEach
    public void setUp() {
        given(tokenProperties.getRefreshTokenValidity()).willReturn(86_400_000L);
    }

    // createRefreshToken
    @Test
    public void createRefreshToken_ShouldReturnToken() {
        given(refreshTokenRepository.findByUserId(anyLong())).willReturn(Optional.empty());

        String generatedToken = "someRefreshToken";
        given(jwtTokenService.buildRefreshToken()).willReturn(generatedToken);
        given(jwtTokenService.getExpiration(generatedToken))
                .willReturn(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));

        RefreshToken refreshToken = refreshTokenService.create(1L, "ip");
        assertNotNull(refreshToken);

        assertEquals(generatedToken, refreshToken.getToken());
        assertEquals("ip", refreshToken.getCreatedByIp());
        assertEquals(1L, refreshToken.getUserId());
        assertTrue(refreshToken.getExpiresAt().isAfter(LocalDateTime.now()));
    }

    // saveRefreshToken
    @Test
    public void save_ShouldReturnSavedRefreshToken() {
        RefreshToken refreshToken = mock(RefreshToken.class);
        given(refreshTokenRepository.save(any(RefreshToken.class))).willReturn(refreshToken);

        RefreshToken token = refreshTokenService.saveRefreshToken(refreshToken);
        assertNotNull(token);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    // findRefreshTokenByValue
    @Test
    public void findRefreshTokenByValue_ShouldReturnEmpty() {
        given(refreshTokenRepository.findByToken(anyString())).willReturn(Optional.empty());
        assertEquals(Optional.empty(), refreshTokenService.findRefreshTokenByValue("token"));
    }

    @Test
    public void findRefreshTokenByValue_InvalidToken_ShouldReturnEmpty() {
        RefreshToken refreshToken = mock(RefreshToken.class);
        given(refreshToken.getToken()).willReturn("token");

        given(refreshTokenRepository.findByToken(anyString())).willReturn(Optional.of(refreshToken));
        given(jwtTokenService.isTokenValid(refreshToken.getToken())).willReturn(false);

        assertEquals(Optional.empty(), refreshTokenService.findRefreshTokenByValue("token"));
    }

    @Test
    public void findRefreshTokenByValue_ShouldReturnToken() {
        RefreshToken refreshToken = mock(RefreshToken.class);
        given(refreshToken.getToken()).willReturn("token");

        given(refreshTokenRepository.findByToken(anyString())).willReturn(Optional.of(refreshToken));
        given(jwtTokenService.isTokenValid(refreshToken.getToken())).willReturn(true);

        RefreshToken token = refreshTokenService.findRefreshTokenByValue("token").orElse(null);
        assertNotNull(token);
    }

}
