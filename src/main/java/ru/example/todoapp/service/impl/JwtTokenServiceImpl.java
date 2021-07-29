package ru.example.todoapp.service.impl;
/*
 * Date: 3/25/21
 * Time: 1:05 PM
 * */

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import ru.example.todoapp.config.properties.TokenProperties;
import ru.example.todoapp.entity.User;
import ru.example.todoapp.enums.Role;
import ru.example.todoapp.exception.CustomException;
import ru.example.todoapp.security.UserDetailsImpl;
import ru.example.todoapp.service.JwtTokenService;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {

    private final TokenProperties tokenProperties;

    public JwtTokenServiceImpl(TokenProperties tokenProperties) {
        this.tokenProperties = tokenProperties;
    }

    @Override
    public String buildAccessToken(Long userId, Set<Role> roles) {
        Claims claims = Jwts.claims();
        claims.put("id", userId);
        claims.put("auth", roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                .collect(Collectors.toList()));

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(getValidity(tokenProperties.getAccessTokenValidity()))
                .signWith(getSecretKey())
                .compact();
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
        return "";
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
            throw CustomException.forbidden("Expired/Invalid JWT token");
        }
    }

    @PostConstruct
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }


    @Override
    public Long getUserIdFromAccessToken(String accessToken) {
        Claims claims = getClaimsBody(accessToken);
        return Long.parseLong(String.valueOf(claims.get("id")));
    }

    @Override
    public Set<Role> getUserRolesFromAccessToken(String accessToken) {
        Claims claims = getClaimsBody(accessToken);
        return extractRoles(claims);
    }

    @Override
    public Authentication getAuthentication(String accessToken) {
        Claims claims = getClaimsBody(accessToken);

        Long id = Long.parseLong(String.valueOf(claims.get("id")));
        Set<Role> roles = extractRoles(claims);

        User principal = new User();
        principal.setId(id);
        principal.setRoles(roles);
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
