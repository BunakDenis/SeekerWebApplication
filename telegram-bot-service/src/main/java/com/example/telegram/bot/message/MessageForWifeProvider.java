package com.example.telegram.bot.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@Log4j
@Component
public class MessageForWifeProvider {

    private static int msgCount;

    public static String getMessage() {

        String result = "";

        switch (msgCount) {
            case 0:
              result = "Зайка, ты у меня самая красивая";
              break;
            case 1:
                result = "Зайка ты самая прекрасная";
                break;
            case 2:
                result = "Зайка, ты самая лучшая, просто поверь в это!!!";
                break;
            case 3:
                result = "Зайка, ты самая обворожительная! Но с этим лучше по аккуратней!";
                break;
            case 4:
                result = "Зайка, я тебя сильно ЛЮБЛЮ!!!";
                break;
            default:
                result = "Ты самый дорогой и в то же время самый бесценный диамант в этом мире!!";
                break;
        }

        msgCount++;

        return result;

    }

}
