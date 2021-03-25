package ru.example.todo.config.properties;
/*
 * Date: 3/25/21
 * Time: 1:45 PM
 * */

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "token")
public class TokenProperties {

    private String secret;

    private long accessTokenValidity;

    private long refreshTokenValidity;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getAccessTokenValidity() {
        return accessTokenValidity;
    }

    public void setAccessTokenValidity(long accessTokenValidity) {
        this.accessTokenValidity = accessTokenValidity;
    }

    public long getRefreshTokenValidity() {
        return refreshTokenValidity;
    }

    public void setRefreshTokenValidity(long refreshTokenValidity) {
        this.refreshTokenValidity = refreshTokenValidity;
    }
}
