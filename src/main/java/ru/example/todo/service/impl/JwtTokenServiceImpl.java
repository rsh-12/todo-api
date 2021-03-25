package ru.example.todo.service.impl;
/*
 * Date: 3/25/21
 * Time: 1:05 PM
 * */

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.RandomStringUtils;
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

    private SecretKey secretKey;

    public JwtTokenServiceImpl(TokenProperties tokenProperties, UserDetailsServiceImpl userDetailsService, UserRepository userRepository, TokenStore tokenStore) {
        this.tokenProperties = tokenProperties;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.tokenStore = tokenStore;
    }

    @PostConstruct
    protected void init() {
        secretKey = Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String buildAccessToken(String username, Set<Role> roles) {

        Claims claims = Jwts.claims();
        claims.put("username", username);
        claims.put("auth", roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                .collect(Collectors.toList()));

        Date validity = new Date(System.currentTimeMillis() + tokenProperties.getAccessTokenValidity());

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(validity)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public RefreshToken buildRefreshToken(String username) {

        if (!userRepository.existsByUsername(username)) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        Date validity = new Date(System.currentTimeMillis() + tokenProperties.getRefreshTokenValidity());

        RefreshToken refreshToken = new RefreshToken.Builder()
                .id(RandomStringUtils.randomAlphanumeric(64))
                .expiryTime(validity)
                .username(username)
                .build();

        tokenStore.save(refreshToken);
        return refreshToken;
    }


    @Override
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey).build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            throw new CustomException("Expired or invalid JWT token");
        }
    }

    @Override
    public RefreshToken findRefreshTokenById(String tokenId) {
        return tokenStore.findById(tokenId);
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
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody().getSubject();
    }
}
