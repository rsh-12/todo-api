package ru.example.todo.service;
/*
 * Date: 7/3/21
 * Time: 1:41 PM
 * */

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.example.todo.entity.RefreshToken;
import ru.example.todo.repository.RefreshTokenRepository;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class RefreshTokenServiceTest extends AbstractServiceTestClass {

    @Autowired
    private RefreshTokenService refreshTokenService;

    @MockBean
    private RefreshTokenRepository refreshTokenRepository;

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
    // findRefreshTokenByUserId
    // hasRefreshTokenExpired


}
