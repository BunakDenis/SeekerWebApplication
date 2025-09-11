package com.example.database;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {"com.example.database", "com.example.utils", "com.example.data.models"})
public class DataProviderService {
    public static void main(String[] args) {
        SpringApplication.run(DataProviderService.class, args);
    }
}
