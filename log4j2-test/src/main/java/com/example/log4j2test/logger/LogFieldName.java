package com.example.log4j2test.logger;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-12-23 15:21
 */
public final class LogFieldName {

    public static final String TRACE_ID = "trace_id";
    public static final String REQUEST_ID = "request_id";
    public static final String REQUEST_URI = "request_uri";
    public static final String REQUEST_METHOD = "request_method";
    public static final String UPSTREAM_APP_CODE = "upstream_app_code";
    public static final String LOGIN_TOKEN = "login_token";
    public static final String LOCAL_IP = "local_ip";
    public static final String USER_ID = "user_id";
    public static final String MOBILE = "mobile";
    public static final String ORDER_NUMBER = "order_number";
    public static final String INTERCEPT_CLASS = "intercept_class";


    public static final String TRACE_ID_HEADER = "x-trace-id";
    public static final String APP_CODE_HEADER = "x-app-code";
    public static final String USER_ID_HEADER = "x-user-id";
    public static final String LOGIN_TOKEN_HEADER = "Authorization";

    public static final String METHOD_NAME = "method_name";
    public static final String LOG_TYPE = "log_type";
    public static final String SOURCE = "source";
    public static final String RESPONSE_BODY = "response_body";

    public static final String APP_NAME = "app_name";

    private LogFieldName() {
    }
}
