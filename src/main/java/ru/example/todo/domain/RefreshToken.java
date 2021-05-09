package ru.example.todo.domain;
/*
 * Date: 3/25/21
 * Time: 10:47 AM
 * */

import java.util.Date;
import java.util.Objects;

public class RefreshToken {

    private final String token;
    private final String username;
    private final Date expiryTime;

    public RefreshToken(String token, String username, Date expiryTime) {
        this.token = token;
        this.username = username;
        this.expiryTime = expiryTime;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RefreshToken that = (RefreshToken) o;

        if (!Objects.equals(token, that.token)) return false;
        return Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        int result = token != null ? token.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        return result;
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
