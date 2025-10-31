package com.example.server;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.example.server", "com.example.utils", "com.example.data.models"})
public class WebServerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebServerServiceApplication.class, args);
    }
}
