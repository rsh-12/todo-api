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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
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

    @BeforeEach
    public void setUp() {
        given(tokenProperties.getRefreshTokenValidity()).willReturn(86_400_000L);
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

}
