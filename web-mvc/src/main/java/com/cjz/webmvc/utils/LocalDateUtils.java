package com.cjz.webmvc.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-10-20 19:50
 */
public class LocalDateUtils {
    public static DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static DateTimeFormatter YYYYMMDDHHMMSS = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    public static DateTimeFormatter YYYY_MM_DD_HHMMSS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static LocalDateTime dateToLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.toInstant(ZoneOffset.of("+8")));
    }

    public static LocalDateTime toLocalDateTime(String dateStr, DateTimeFormatter dtf) {
        return LocalDateTime.parse(dateStr, dtf);
    }

    public static LocalDateTime dayStart() {
        return LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
    }


    /**
     * 当月的第一天
     *
     * @return ig
     */
    public static LocalDate monthStart() {
        final LocalDate now = LocalDate.now();
        return LocalDate.of(now.getYear(), now.getMonth(), 1);
    }

    /**
     * 当月的最后一天
     *
     * @return ig
     */
    public static LocalDate monthEnd() {
        final LocalDate now = LocalDate.now();
        return LocalDate.of(now.getYear(), now.getMonth(), now.getMonth().length(now.isLeapYear()));
    }

    public static String monthStartStr() {
        return monthStart().format(YYYY_MM_DD);
    }

    public static String monthEndStr() {
        return monthEnd().format(YYYY_MM_DD);
    }


}
