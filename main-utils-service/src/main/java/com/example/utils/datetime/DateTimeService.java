package com.example.utils.datetime;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class DateTimeService {

    private static final String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

    public static String getNowDateTime() {
        return LocalDateTime.now().format(formatter);
    }

}
