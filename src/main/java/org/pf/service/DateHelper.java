package org.pf.service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateHelper {

    static ZonedDateTime getStartDate(String year) {
        return ZonedDateTime.parse(year + "-01-01 00:00:00.0", DateTimeFormatter.ofPattern(
            ChartsService.YYYY_MM_DD_HH_MM_SS_S).withZone(ZoneId.systemDefault()));
    }

    static ZonedDateTime getEndDate(String year) {
        return ZonedDateTime.parse(year + ChartsService.END_OF_MONTH_STRING, DateTimeFormatter.ofPattern(
            ChartsService.YYYY_MM_DD_HH_MM_SS_S).withZone(ZoneId.systemDefault()));
    }

    static ZonedDateTime getStartDate(String year, String month) {
        String dateString = String.format("%s-%02d-01 00:00:00.0", year, Integer.parseInt(month));
        return ZonedDateTime.parse(dateString, DateTimeFormatter.ofPattern(
            ChartsService.YYYY_MM_DD_HH_MM_SS_S).withZone(ZoneId.systemDefault()));
    }

    static ZonedDateTime getEndDate(String year, String month) {
        return ZonedDateTime.parse(String.format("%s-%02d-31 23:59:59.0", year, Integer.parseInt(month)), DateTimeFormatter.ofPattern(
            ChartsService.YYYY_MM_DD_HH_MM_SS_S).withZone(ZoneId.systemDefault()));
    }

    static ZonedDateTime getZonedDateTime(Integer year) {
        return ZonedDateTime.parse(year + "-01-01 00:00:00.0", DateTimeFormatter.ofPattern(
                  ChartsService.YYYY_MM_DD_HH_MM_SS_S).withZone(ZoneId.systemDefault()));
    }
}
