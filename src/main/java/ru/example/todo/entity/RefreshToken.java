package ru.example.todo.entity;
/*
 * Date: 3/25/21
 * Time: 10:47 AM
 * */

import java.time.Duration;
import java.util.Date;

import static java.time.Instant.now;

public class RefreshToken {

    private String id;

    private String username;

    private Date expiryTime;

    private long getValiditySeconds() {
        return expiryTime == null ? Long.MAX_VALUE : Duration.between(now(), expiryTime.toInstant()).getSeconds();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(Date expiryTime) {
        this.expiryTime = expiryTime;
    }

    @Override
    public String toString() {
        return "RefreshToken{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", expiryTime=" + expiryTime +
                '}';
    }
}
