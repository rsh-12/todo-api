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
import ru.example.todo.repository.RefreshTokenRepository;
import ru.example.todo.service.impl.RefreshTokenServiceImpl;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    public void setUp() throws Exception {
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
