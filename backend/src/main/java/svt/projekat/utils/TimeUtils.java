package svt.projekat.utils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static String localTimeToString(LocalTime time) {
        return time != null ? time.format(TIME_FORMATTER) : "";
    }

    public static LocalTime stringToLocalTime(String timeString) {
        return LocalTime.parse(timeString, TIME_FORMATTER);
    }
}
