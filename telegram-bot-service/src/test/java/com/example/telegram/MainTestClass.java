package com.example.telegram;

import com.example.telegram.bot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        classes = TestTelegramBotApplication.class
)
public abstract class MainTestClass {

    @Autowired
    private UserService userService;

}
