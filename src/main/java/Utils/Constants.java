package Utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Constants {
    public static final String BASE_URI = "https://qa-scooter.education-services.ru";
    public static final String CREATE_ENDPOINT_COURIER = "/api/v1/courier";
    public static final String LOGIN_ENDPOINT_COURIER = "/api/v1/courier/login";
    public static final String CREATE_ENDPOINT_ORDERS = "/api/v1/orders";
    public static final String GET_ENDPOINT_ORDERS_TRACK ="/api/v1/orders/track?t=";
    public static final String PUT_ENDPOINT_ORDER_ACCEPT ="/api/v1/orders/accept";

    public static final int OK_STATUS_COD= 200;
    public static final int CREATE_STATUS_COD= 201;
    public static final int CONFLICT_STATUS_COD = 409;
    public static final int BAD_REQUEST_STATUS_COD = 400;
    public static final int NOT_FOUND_STATUS_COD = 404;
    public static final int INTERNAL_SERVER_ERROR_STATUS_COD = 500;

    public static final int ONE_DAY = 1;
    public static final int TWO_DAYS = 2;
    public static final int THREE_DAYS = 3;
    public static final int FOUR_DAYS = 4;
    public static final int FIVE_DAYS = 5;
    public static final int SIX_DAYS = 6;
    public static final int SEVEN_DAYS = 7;

    public static final LocalDate TOMORROW = LocalDate.now().plusDays(1);
    public static final String TOMORROW_STRING = TOMORROW.format(DateTimeFormatter.ISO_LOCAL_DATE);
}
