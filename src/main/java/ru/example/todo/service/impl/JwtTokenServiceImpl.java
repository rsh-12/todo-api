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
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import ru.example.todo.config.properties.TokenProperties;
import ru.example.todo.domain.RefreshToken;
import ru.example.todo.entity.User;
import ru.example.todo.enums.Role;
import ru.example.todo.exception.CustomException;
import ru.example.todo.security.UserDetailsImpl;
import ru.example.todo.service.JwtTokenService;
import ru.example.todo.service.TokenStore;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import static java.time.Instant.now;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {

    private final TokenProperties tokenProperties;
    private final TokenStore tokenStore;

    public JwtTokenServiceImpl(TokenProperties tokenProperties, TokenStore tokenStore) {
        this.tokenProperties = tokenProperties;
        this.tokenStore = tokenStore;
    }

    @Override
    public String buildAccessToken(Long userId, Set<Role> roles) {
        Claims claims = Jwts.claims();
        claims.put("id", userId);

        if (roles != null) {
            claims.put("auth", roles.stream()
                    .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                    .collect(Collectors.toList()));
        }

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(getValidity(tokenProperties.getAccessTokenValidity()))
                .signWith(getSecretKey())
                .compact();
    }

    // todo: replace random alphanumeric with jwts
    @Override
    public RefreshToken buildRefreshToken(String username) {
        String randomToken = RandomStringUtils.randomAlphanumeric(64);

        long refreshTokenValidity = tokenProperties.getRefreshTokenValidity();
        Date expiryTime = getValidity(refreshTokenValidity);

        RefreshToken refreshToken = new RefreshToken(randomToken, username, expiryTime);
        tokenStore.saveRefreshToken(refreshToken);

        return refreshToken;
    }

    private static Date getValidity(long millis) {
        Date now = new Date();
        return new Date(now.getTime() + millis);
    }

    @Override
    public String resolveAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null) {
            return (bearerToken.startsWith("Bearer") || bearerToken.startsWith("bearer")) ?
                    bearerToken.substring(7) :
                    bearerToken;
        }
        return null;
    }

    @Override
    public boolean isAccessTokenValid(String accessToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(accessToken);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            throw new CustomException("Forbidden", "Expired or invalid JWT token", HttpStatus.FORBIDDEN);
        }
    }

    @PostConstruct
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public RefreshToken findRefreshToken(String refreshToken) {
        if (refreshToken == null) {
            throw new CustomException("Bad Request", "Invalid token", HttpStatus.BAD_REQUEST);
        }
        return tokenStore.findRefreshToken(refreshToken);
    }

    @Override
    public void removeRefreshTokenById(String tokenId) {
        tokenStore.deleteRefreshTokenById(tokenId);
    }

    @Override
    public boolean hasRefreshTokenExpired(RefreshToken refreshToken) {
        return now().isBefore(refreshToken.getExpiryTime().toInstant());
    }

    @Override
    public Long getId(String accessToken) {
        Claims claims = getClaimsBody(accessToken);
        return Long.parseLong(String.valueOf(claims.get("id")));
    }

    @Override
    public Set<Role> getUserRoles(String accessToken) {
        Claims claims = getClaimsBody(accessToken);
        return extractRoles(claims);
    }

    @Override
    public Authentication getAuthentication(String accessToken) {
        Claims claims = getClaimsBody(accessToken);

        Long id = Long.parseLong(String.valueOf(claims.get("id")));
        Set<Role> roles = extractRoles(claims);

        User principal = new User(id, roles);
        UserDetailsImpl userDetails = new UserDetailsImpl(principal);

        return new UsernamePasswordAuthenticationToken(userDetails, "", roles);
    }

    private Claims getClaimsBody(String accessToken) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(accessToken)
                .getBody();
    }

    private Set<Role> extractRoles(Claims claims) {
        String[] authorities = claims.get("auth").toString().split(",");
        return Arrays.stream(authorities)
                .map(authority -> authority
                        .replaceAll("[{}\\[\\] ]", "")
                        .replaceAll("authority=", ""))
                .map(Role::valueOf)
                .collect(Collectors.toSet());
    }

}
