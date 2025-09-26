package com.example.data.models.consts;

import lombok.Data;
import java.text.MessageFormat;

@Data
public class DataProviderEndpointsConsts {

    private static final String API_USER_ENDPOINT = "/user/{0}";
    private static final String API_TELEGRAM_USER_ENDPOINT = "/telegram_user/{0}";
    private static final String API_CHAT_ENDPOINT = "/chat/{0}";
    private static final String API_SESSION_ENDPOINT = "/session/{0}";
    private static final String API_TRANSIENT_SESSION_ENDPOINT = "/transient_session/{0}";
    private static final String API_PERSISTENT_SESSION_ENDPOINT = "/persistent_session/{0}";
    private static final String API_OTP_CODE_ENDPOINT = "/otp_code/{0}";

    public static String getApiUserEndpoint(String path) {
        return MessageFormat.format(API_USER_ENDPOINT, path);
    }
    public static String getApiTelegramUserEndpoint(String path) {
        return MessageFormat.format(API_TELEGRAM_USER_ENDPOINT, path);
    }
    public static String getApiChatEndpoint(String path) {
        return MessageFormat.format(API_CHAT_ENDPOINT, path);
    }
    public static String getApiSessionEndpoint(String path) {
        return MessageFormat.format(API_SESSION_ENDPOINT, path);
    }
    public static String getApiTransientSessionEndpoint(String path) {return MessageFormat.format(API_TRANSIENT_SESSION_ENDPOINT, path);}
    public static String getApiPersistentSessionEndpoint(String path) {return MessageFormat.format(API_PERSISTENT_SESSION_ENDPOINT, path);}
    public static String getApiOtpCodeEndpoint(String path) {
        return MessageFormat.format(API_OTP_CODE_ENDPOINT, path);
    }



}
