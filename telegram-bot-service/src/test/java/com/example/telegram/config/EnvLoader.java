package com.example.telegram.config;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvLoader {
    public static final Dotenv DOTENV = Dotenv.configure()
            .directory("../.env")
            .ignoreIfMalformed()
            .ignoreIfMissing()
            .load();
}
