package com.onesolutions.dsd.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    /**
     * Configure a custom thread pool for async email operations.
     * This ensures emails are sent in background threads without blocking the main request.
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);                  // Number of threads to keep alive
        executor.setMaxPoolSize(10);                  // Maximum number of threads
        executor.setQueueCapacity(100);               // Queue size for pending tasks
        executor.setThreadNamePrefix("async-email-"); // Thread name prefix for debugging
        executor.initialize();
        return executor;
    }
}

