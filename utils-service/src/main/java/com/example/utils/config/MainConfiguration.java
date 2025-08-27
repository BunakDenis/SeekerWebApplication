package com.example.utils.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@Slf4j
public class MainConfiguration {

    @Value("${spring.profiles.active}")
    private String activeSpringProfile;

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Value("${spring.mail.password}")
    private String mailUserPassword;

    @Bean
    public JavaMailSender getJavaMailSender() {

        log.debug("Username = {}", mailUsername);
        log.debug("User password = {}", mailUserPassword);

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        Properties props = mailSender.getJavaMailProperties();

        if (activeSpringProfile.equals("prod")) {

            mailSender.setHost("smtp.migadu.com");
            mailSender.setPort(587);
            mailSender.setUsername(mailUsername);
            mailSender.setPassword(mailUserPassword);

        } else {

            mailSender.setHost("smtp.gmail.com");
            mailSender.setPort(587);
            mailSender.setUsername(mailUsername);
            mailSender.setPassword(mailUserPassword);

        }

        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

}
