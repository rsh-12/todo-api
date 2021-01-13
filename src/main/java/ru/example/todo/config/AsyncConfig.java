package ru.example.todo.config;
/*
 * Date: 1/13/21
 * Time: 5:48 PM
 * */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig extends AsyncConfigurerSupport {

    private static final Logger log = LoggerFactory.getLogger(AsyncConfig.class.getName());

    private final AsyncExceptionHandler asyncExceptionHandler;

    public AsyncConfig(AsyncExceptionHandler exceptionHandler) {
        this.asyncExceptionHandler = exceptionHandler;
    }

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        log.info(">>> taskExecutor created...");

        taskExecutor.setCorePoolSize(2); // sets the core number of threads
        taskExecutor.setMaxPoolSize(5); // sets the maximum allowed number of threads
        taskExecutor.setQueueCapacity(100); // sets the capacity for the ThreadPoolTaskExecutor's BlockingQueue
        taskExecutor.setThreadNamePrefix("todoThread-");
        taskExecutor.initialize(); // set up the ExecutorService
        return taskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return asyncExceptionHandler;
    }
}
