package com.example.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.*;

@Configuration
public class ExecutorConfig {

    @Value("${app.executor.pool-size:10}")
    private int poolSize;

    @Value("${app.executor.queue-capacity:500}")
    private int queueCapacity;

    @Bean(destroyMethod = "shutdown")
    public ExecutorService taskExecutor() {
        // return Executors.newFixedThreadPool(poolSize);
        // Usando ThreadPoolExecutor diretamente para definir um limite na fila (Queue Capacity)
        // Isso previne OutOfMemoryError caso o consumidor seja mais lento que o produtor.
        return new ThreadPoolExecutor(poolSize, poolSize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(queueCapacity));
    }
}