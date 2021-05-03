package ru.example.todo.service.impl;
/*
 * Date: 3/25/21
 * Time: 1:05 PM
 * */

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.example.todo.config.properties.TokenProperties;
import ru.example.todo.entity.RefreshToken;
import ru.example.todo.entity.Role;
import ru.example.todo.exception.CustomException;
import ru.example.todo.repository.UserRepository;
import ru.example.todo.security.UserDetailsServiceImpl;
import ru.example.todo.service.JwtTokenService;
import ru.example.todo.service.TokenStore;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import static java.time.Instant.now;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {

    private final UserDetailsServiceImpl userDetailsService;
    private final TokenProperties tokenProperties;
    private final UserRepository userRepository;
    private final TokenStore tokenStore;

    public JwtTokenServiceImpl(TokenProperties tokenProperties, UserDetailsServiceImpl userDetailsService,
                               UserRepository userRepository, TokenStore tokenStore) {
        this.tokenProperties = tokenProperties;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.tokenStore = tokenStore;
    }

    @Override
    public String buildAccessToken(String username, Set<Role> roles) {

        Claims claims = Jwts.claims();
        claims.put("username", username);
        claims.put("auth", roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                .collect(Collectors.toList()));

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(getValidity(tokenProperties.getAccessTokenValidity()))
                .signWith(getSecretKey())
                .compact();
    }

    @Override
    public RefreshToken buildRefreshToken(String username) {

        RefreshToken refreshToken = new RefreshToken.Builder()
                .id(RandomStringUtils.randomAlphanumeric(64))
                .expiryTime(getValidity(tokenProperties.getRefreshTokenValidity()))
                .username(username)
                .build();

        tokenStore.save(refreshToken);
        return refreshToken;
    }

    private static Date getValidity(long millis) {
        Date now = new Date();
        return new Date(now.getTime() + millis);
    }


    @Override
    public Authentication getAuthentication(String token) {
        String username = getUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && StringUtils.startsWithIgnoreCase(bearerToken, "Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            throw new CustomException("Expired or invalid JWT token", HttpStatus.FORBIDDEN);
        }
    }

    @PostConstruct
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public RefreshToken findRefreshToken(String token) {
        return tokenStore.find(token);
    }

    @Override
    public void removeOldRefreshTokenById(String tokenId) {
        tokenStore.deleteById(tokenId);
    }

    @Override
    public boolean isValidRefreshToken(RefreshToken refreshToken) {
        return !now().isAfter(refreshToken.getExpiryTime().toInstant());
    }

    private String getUsername(String token) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return String.valueOf(claims.get("username"));
    }
}
