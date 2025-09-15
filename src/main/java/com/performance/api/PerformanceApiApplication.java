package com.performance.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableCaching
@EnableTransactionManagement
public class PerformanceApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PerformanceApiApplication.class, args);
    }
}
