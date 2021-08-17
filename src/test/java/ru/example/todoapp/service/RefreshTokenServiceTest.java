package ru.example.todoapp.service;
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
import ru.example.todoapp.config.properties.TokenProperties;
import ru.example.todoapp.entity.RefreshToken;
import ru.example.todoapp.repository.RefreshTokenRepository;
import ru.example.todoapp.service.impl.RefreshTokenServiceImpl;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
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
    /*@Test
    public void createRefreshToken_ShouldReturnNewRefreshTokenValue() {
        RefreshToken mockRefreshToken = mock(RefreshToken.class);
        given(refreshTokenRepository.findByUserId(anyLong())).willReturn(Optional.empty());
        given(refreshTokenRepository.save(any(RefreshToken.class))).willReturn(mockRefreshToken);

        String refreshTokenValue = refreshTokenService.createRefreshToken(1L, "0.0.0.0:8080");
        assertNotNull(refreshTokenValue);
        assertEquals(64, refreshTokenValue.length());

        verify(refreshTokenRepository).findByUserId(anyLong());
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }*/


/*
    @Test
    public void createRefreshToken_ShouldReturnUpdatedRefreshTokenValue() {
        RefreshToken mockRefreshToken = mock(RefreshToken.class);
        given(refreshTokenRepository.findByUserId(anyLong())).willReturn(Optional.of(mockRefreshToken));
        given(refreshTokenRepository.save(any(RefreshToken.class))).willReturn(mockRefreshToken);

        RefreshToken refreshTokenValue = refreshTokenService.createRefreshToken(1L, null);
        assertNotNull(refreshTokenValue);
//        assertEquals(64, refreshTokenValue.length());

        verify(refreshTokenRepository).findByUserId(anyLong());
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }
*/

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
    /*@Test
    public void findRefreshTokenByValue_ShouldRetunToken() {
        RefreshToken mockRefreshToken = mock(RefreshToken.class);
        given(mockRefreshToken.getValue()).willReturn("someRefreshToken");
        given(mockRefreshToken.getExpiresAt()).willReturn(LocalDateTime.now().plusSeconds(60));

        given(refreshTokenRepository.findByValue(anyString())).willReturn(Optional.of(mockRefreshToken));
        RefreshToken refreshToken = refreshTokenService.findRefreshTokenByValue(mockRefreshToken.getValue());

        assertNotNull(refreshToken);
        assertEquals("someRefreshToken", refreshToken.getValue());
    }*/
/*
    @Test
    public void findRefreshTokenByValue_NotFound_ShouldThrowCustomException() {
        given(refreshTokenRepository.findByValue(anyString())).willReturn(Optional.empty());
        assertThrows(CustomException.class, () ->
                refreshTokenService.findRefreshTokenByValue("someRefreshToken"));
    }*/

    /*@Test
    public void findRefreshTokenByValue_Expired_ShouldThrowCustomException() {
        RefreshToken mockRefreshToken = mock(RefreshToken.class);
        given(mockRefreshToken.getValue()).willReturn("someRefreshToken");
        given(mockRefreshToken.getExpiresAt()).willReturn(LocalDateTime.now().minusSeconds(60));

        given(refreshTokenRepository.findByValue(anyString())).willReturn(Optional.of(mockRefreshToken));
        assertThrows(CustomException.class, () ->
                refreshTokenService.findRefreshTokenByValue(mockRefreshToken.getValue()));
    }*/

    // findRefreshTokenByUserId
    /*@Test
    public void findRefreshTokenByUserId_ShouldRetunToken() {
        RefreshToken mockRefreshToken = mock(RefreshToken.class);
        given(mockRefreshToken.getUserId()).willReturn(1L);
        given(mockRefreshToken.getExpiresAt()).willReturn(LocalDateTime.now().plusSeconds(60));

        given(refreshTokenRepository.findByUserId(anyLong())).willReturn(Optional.of(mockRefreshToken));
        RefreshToken refreshToken = refreshTokenService.findRefreshTokenByUserId(mockRefreshToken.getUserId());

        assertNotNull(refreshToken);
        assertEquals(1L, (long) refreshToken.getUserId());
    }*/

    /*@Test
    public void findRefreshTokenByUserId_NotFound_ShouldThrowCustomException() {
        given(refreshTokenRepository.findByUserId(anyLong())).willReturn(Optional.empty());
        assertThrows(CustomException.class, () ->
                refreshTokenService.findRefreshTokenByUserId(1L));
    }*/

    /*@Test
    public void findRefreshTokenByUserId_Expired_ShouldThrowCustomException() {
        RefreshToken mockRefreshToken = mock(RefreshToken.class);
        given(mockRefreshToken.getUserId()).willReturn(1L);
        given(mockRefreshToken.getExpiresAt()).willReturn(LocalDateTime.now().minusSeconds(60));

        given(refreshTokenRepository.findByUserId(anyLong())).willReturn(Optional.of(mockRefreshToken));
        assertThrows(CustomException.class, () ->
                refreshTokenService.findRefreshTokenByUserId(mockRefreshToken.getUserId()));
    }*/

    // hasRefreshTokenExpired
    /*@Test
    public void hasRefreshTokenExpired_ShouldReturnTrue() {
        RefreshToken refreshToken = mock(RefreshToken.class);
        given(refreshToken.getExpiresAt()).willReturn(LocalDateTime.now().minusSeconds(60));

        assertTrue(refreshTokenService.hasRefreshTokenExpired(refreshToken));
    }*/

    /*@Test
    public void hasRefreshTokenExpired_ShouldReturnFalse() {
        RefreshToken refreshToken = mock(RefreshToken.class);
        given(refreshToken.getExpiresAt()).willReturn(LocalDateTime.now().plusSeconds(60));

        assertFalse(refreshTokenService.hasRefreshTokenExpired(refreshToken));
    }*/

}
