package ru.example.todo.domain;
/*
 * Date: 3/25/21
 * Time: 10:47 AM
 * */

import java.time.Duration;
import java.util.Date;

import static java.time.Instant.now;

public class RefreshToken {

    private final String token;
    private final String username;
    private final Date expiryTime;

    private long getValiditySeconds() {
        return expiryTime == null ? Long.MAX_VALUE : Duration.between(now(), expiryTime.toInstant()).getSeconds();
    }

    public static class Builder {
        private String token;
        private String username;
        private Date expiryTime;

        public Builder token(String val) {
            token = val;
            return this;
        }

        public Builder username(String val) {
            username = val;
            return this;
        }

        public Builder expiryTime(Date val) {
            expiryTime = val;
            return this;
        }

        public RefreshToken build() {
            return new RefreshToken(this);
        }
    }

    private RefreshToken(Builder builder) {
        token = builder.token;
        username = builder.username;
        expiryTime = builder.expiryTime;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public Date getExpiryTime() {
        return expiryTime;
    }

    @Override
    public String toString() {
        return "RefreshToken{" +
                "token='" + token + '\'' +
                ", username='" + username + '\'' +
                ", expiryTime=" + expiryTime +
                '}';
    }

}
