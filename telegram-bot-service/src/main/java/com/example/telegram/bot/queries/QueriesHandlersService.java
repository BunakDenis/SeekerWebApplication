package com.example.telegram.bot.queries;


import com.example.telegram.bot.queries.impl.DecodeAudioQueryHandlerImpl;
import com.example.telegram.bot.queries.impl.UsefulToolsHealsQueryHandlerImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class QueriesHandlersService {

    private final Map<String, QueryHandler> queries;

    public QueriesHandlersService(
            @Autowired DecodeAudioQueryHandlerImpl decodeAudioQuery,
            @Autowired UsefulToolsHealsQueryHandlerImpl usefulToolsHealsQueryHandler
    ) {
        this.queries = Map.of(
                Queries.DECODE_AUDIO.getQuery(), decodeAudioQuery,
                Queries.GET_ACTUAL_USEFUL_TOOLS_HEALS.getQuery(), usefulToolsHealsQueryHandler
        );
    }

    public QueryHandler getQueryHandler(String query) {
        return queries.get(query);
    }

}
