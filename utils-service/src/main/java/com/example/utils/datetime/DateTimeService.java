package com.example.utils.datetime;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Data
public class DateTimeService {

    @Value("${default.utc.zone.id}")
    private static String zoneId;

    public static final String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

    public static String getNowDateTime() {
        return LocalDateTime.now(ZoneId.of(zoneId)).format(formatter);
    }

    public static Long convertDaysToMillis(Long days) {
        return (days * 86400000L);
    }

    public static Long convertMinutesToMillis(Long minutes) {
        return (minutes * 60000L);
    }

}
