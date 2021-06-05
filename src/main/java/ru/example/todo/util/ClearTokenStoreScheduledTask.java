package ru.example.todo.util;
/*
 * Date: 3/25/21
 * Time: 1:24 PM
 * */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.example.todo.service.TokenStore;

@Component
public class ClearTokenStoreScheduledTask {

    private final TokenStore tokenStore;

    @Autowired
    public ClearTokenStoreScheduledTask(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    // Delete expired refresh tokens from memory every day at 01:00 a.m.
    @Scheduled(cron = "0 0 1 * * ?")
    public void clearTokenStore() {
        tokenStore.deleteExpiredRefreshTokens();
    }
}
