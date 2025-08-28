package com.example.telegram.bot.commands;

import com.example.telegram.bot.commands.impl.AuthCommandHandlerImpl;
import com.example.telegram.bot.commands.impl.RegCommandHandlerImpl;
import com.example.telegram.bot.commands.impl.StartCommandHandlerImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class CommandsHandlersService {

    private final Map<String, CommandHandler> commands;

    public CommandsHandlersService(
            @Autowired StartCommandHandlerImpl startCommandHandler,
            @Autowired AuthCommandHandlerImpl authCommandHandler,
            @Autowired RegCommandHandlerImpl regCommandHandler
    ) {
        this.commands = Map.of(
                Commands.START.getCommand(), startCommandHandler,
                Commands.AUTHORIZE.getCommand(), authCommandHandler,
                Commands.REGISTER.getCommand(), regCommandHandler
        );
    }

    public CommandHandler getCommandHandler(String command) {
        return commands.get(command);
    }

}
