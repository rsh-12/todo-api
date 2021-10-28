package ru.example.todoapp.security.filter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.example.todoapp.domain.Role;
import ru.example.todoapp.service.JwtTokenService;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
class JwtTokenFilterTest {

    @Mock
    private JwtTokenService jwtTokenService;

    @InjectMocks
    private JwtTokenFilter jwtTokenFilter;

    @Test
    public void jwtTokenFilterTest() throws ServletException, IOException {
        var auth = new UsernamePasswordAuthenticationToken("user@mail.com", "usersPassword",
                Collections.singletonList(new SimpleGrantedAuthority(Role.USER.name())));

        given(jwtTokenService.resolveAccessToken(any())).willReturn(Optional.of("someAccessToken"));
        given(jwtTokenService.isTokenValid(anyString())).willReturn(true);
        given(jwtTokenService.getAuthentication(anyString())).willReturn(auth);

        var request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + "someAccessToken");
        request.setRequestURI("/api/test");
        jwtTokenFilter.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain());

        assertEquals(SecurityContextHolder.getContext().getAuthentication().getName(), "user@mail.com");
        assertTrue(SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
    }

    @Test
    public void jwtTokenFilterTest_TokenNotProvided() throws ServletException, IOException {
        given(jwtTokenService.resolveAccessToken(any())).willReturn(Optional.empty());
        given(jwtTokenService.isTokenValid(anyString())).willReturn(true);

        var request = new MockHttpServletRequest();
        request.setRequestURI("/api/test");
        jwtTokenFilter.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void jwtTokenFilterTest_TokenNotValid() throws ServletException, IOException {
        given(jwtTokenService.resolveAccessToken(any())).willReturn(Optional.of("SomeAccessToken"));
        given(jwtTokenService.isTokenValid(anyString())).willReturn(false);

        var request = new MockHttpServletRequest();
        request.setRequestURI("/api/test");
        jwtTokenFilter.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

}