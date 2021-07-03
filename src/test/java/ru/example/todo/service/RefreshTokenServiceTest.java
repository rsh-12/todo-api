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

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class RefreshTokenServiceTest extends AbstractServiceTestClass {

    @Autowired
    private RefreshTokenService refreshTokenService;

    @MockBean
    private RefreshTokenRepository refreshTokenRepository;

    // createRefreshToken

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
