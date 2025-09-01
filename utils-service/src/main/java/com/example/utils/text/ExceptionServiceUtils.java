package com.example.utils.text;


import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
public class ExceptionServiceUtils {

    public static String stackTraceToString(Exception e) {
        log.debug("Метод stackTraceToString, Exception {}", e);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        e.printStackTrace(pw);

        log.debug("String stack trace {}", pw);

        return sw.toString();
    }

    public static String stackTraceToString(Throwable e) {
        log.debug("Метод stackTraceToString, Exception {}", e);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        e.printStackTrace(pw);

        log.debug("String stack trace {}", pw);

        return sw.toString();
    }
}
