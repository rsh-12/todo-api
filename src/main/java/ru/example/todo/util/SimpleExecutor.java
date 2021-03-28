package ru.example.todo.util;
/*
 * Date: 3/26/21
 * Time: 5:43 PM
 * */

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

@Component
@EnableAsync
public class SimpleExecutor {

    @Bean
    public Executor taskExecutor() {
        var executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("TokenStoreCleaner-");
        executor.initialize();

        return executor;
    }
}
