package com.example.utils.sender;

import com.example.utils.validator.EmailValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    @Value("${mail.sender.from}")
    private String sendFrom;

    private final JavaMailSender javaMailSender;

    private final EmailValidator emailValidator;

    public void sendSimpleMail(String to, String subject, String text) {

        log.debug("Отправка сообщения " + to + ", от " + sendFrom + ", с текстом " + text);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sendFrom);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        javaMailSender.send(message);
    }

    public boolean isEmailAddressValid(String emailAddress) {
        return emailValidator.isValid(emailAddress);
    }

}
