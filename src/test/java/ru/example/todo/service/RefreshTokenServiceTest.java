package ru.example.todo.service;
/*
 * Date: 7/3/21
 * Time: 1:41 PM
 * */

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ru.example.todo.config.properties.TokenProperties;
import ru.example.todo.entity.RefreshToken;
import ru.example.todo.exception.CustomException;
import ru.example.todo.repository.RefreshTokenRepository;
import ru.example.todo.service.impl.RefreshTokenServiceImpl;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RefreshTokenServiceTest {

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private TokenProperties tokenProperties;

    @Before
    public void setUp() {
        given(tokenProperties.getRefreshTokenValidity()).willReturn(86_400_000L);
    }

    // createRefreshToken
    @Test
    public void createRefreshToken_ShouldReturnNewRefreshTokenValue() {
        RefreshToken mockRefreshToken = mock(RefreshToken.class);
        given(refreshTokenRepository.findByUserId(anyLong())).willReturn(Optional.empty());
        given(refreshTokenRepository.save(any(RefreshToken.class))).willReturn(mockRefreshToken);

        String refreshTokenValue = refreshTokenService.createRefreshToken(1L, "0.0.0.0:8080");
        assertNotNull(refreshTokenValue);
        assertEquals(64, refreshTokenValue.length());

        verify(refreshTokenRepository).findByUserId(anyLong());
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    public void createRefreshToken_ShouldReturnUpdatedRefreshTokenValue() {
        RefreshToken mockRefreshToken = mock(RefreshToken.class);
        given(refreshTokenRepository.findByUserId(anyLong())).willReturn(Optional.of(mockRefreshToken));
        given(refreshTokenRepository.save(any(RefreshToken.class))).willReturn(mockRefreshToken);

        String refreshTokenValue = refreshTokenService.createRefreshToken(1L, null);
        assertNotNull(refreshTokenValue);
        assertEquals(64, refreshTokenValue.length());

        verify(refreshTokenRepository).findByUserId(anyLong());
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    // save
    @Test
    public void save_ShouldReturnSavedRefreshToken() {
        RefreshToken refreshToken = mock(RefreshToken.class);
        given(refreshTokenRepository.save(any(RefreshToken.class))).willReturn(refreshToken);

        RefreshToken token = refreshTokenService.save(refreshToken);
        assertNotNull(token);

        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }


    // findRefreshTokenByValue
    @Test
    public void findRefreshTokenByValue_ShouldRetunToken() {
        RefreshToken mockRefreshToken = mock(RefreshToken.class);
        given(mockRefreshToken.getValue()).willReturn("someRefreshToken");
        given(mockRefreshToken.getExpiresAt()).willReturn(Date.from(new Date().toInstant().plusSeconds(60)));

        given(refreshTokenRepository.findByValue(anyString())).willReturn(Optional.of(mockRefreshToken));
        RefreshToken refreshToken = refreshTokenService.findRefreshTokenByValue(mockRefreshToken.getValue());

        assertNotNull(refreshToken);
        assertEquals("someRefreshToken", refreshToken.getValue());
    }

    @Test
    public void findRefreshTokenByValue_NotFound_ShouldThrowCustomException() {
        given(refreshTokenRepository.findByValue(anyString())).willReturn(Optional.empty());
        assertThrows(CustomException.class, () ->
                refreshTokenService.findRefreshTokenByValue("someRefreshToken"));
    }

    @Test
    public void findRefreshTokenByValue_Expired_ShouldThrowCustomException() {
        RefreshToken mockRefreshToken = mock(RefreshToken.class);
        given(mockRefreshToken.getValue()).willReturn("someRefreshToken");
        given(mockRefreshToken.getExpiresAt()).willReturn(Date.from(new Date().toInstant().minusSeconds(60)));

        given(refreshTokenRepository.findByValue(anyString())).willReturn(Optional.of(mockRefreshToken));
        assertThrows(CustomException.class, () ->
                refreshTokenService.findRefreshTokenByValue(mockRefreshToken.getValue()));
    }

    // findRefreshTokenByUserId
    @Test
    public void findRefreshTokenByUserId_ShouldRetunToken() {
        RefreshToken mockRefreshToken = mock(RefreshToken.class);
        given(mockRefreshToken.getUserId()).willReturn(1L);
        given(mockRefreshToken.getExpiresAt()).willReturn(Date.from(new Date().toInstant().plusSeconds(60)));

        given(refreshTokenRepository.findByUserId(anyLong())).willReturn(Optional.of(mockRefreshToken));
        RefreshToken refreshToken = refreshTokenService.findRefreshTokenByUserId(mockRefreshToken.getUserId());

        assertNotNull(refreshToken);
        assertEquals(1L, (long) refreshToken.getUserId());
    }

    // hasRefreshTokenExpired
    @Test
    public void hasRefreshTokenExpired_ShouldReturnTrue() {
        RefreshToken refreshToken = mock(RefreshToken.class);
        Instant instant = new Date().toInstant().minusSeconds(60);
        given(refreshToken.getExpiresAt()).willReturn(Date.from(instant));
        assertTrue(refreshTokenService.hasRefreshTokenExpired(refreshToken));
    }

    @Test
    public void hasRefreshTokenExpired_ShouldReturnFalse() {
        RefreshToken refreshToken = mock(RefreshToken.class);
        Instant instant = new Date().toInstant().plusSeconds(60);
        given(refreshToken.getExpiresAt()).willReturn(Date.from(instant));
        assertFalse(refreshTokenService.hasRefreshTokenExpired(refreshToken));
    }

}
