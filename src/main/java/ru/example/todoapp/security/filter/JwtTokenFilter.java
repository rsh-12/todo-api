package ru.example.todoapp.security.filter;
/*
 * Date: 3/25/21
 * Time: 1:04 PM
 * */

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.example.todoapp.exception.CustomException;
import ru.example.todoapp.service.JwtTokenService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;

    public JwtTokenFilter(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        String accessToken = jwtTokenService.resolveAccessToken(httpServletRequest).orElse("");

        try {
            if (!accessToken.isEmpty() && jwtTokenService.isTokenValid(accessToken)) {
                Authentication auth = jwtTokenService.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (CustomException ex) {
            SecurityContextHolder.clearContext();
//            httpServletResponse.sendError(HttpStatus.BAD_REQUEST.value(), "Invalid token");
            return;
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

}
