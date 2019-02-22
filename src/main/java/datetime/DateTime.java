package datetime;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateTime {
  public static String formatTime (String timeStr) {
    LocalTime time = LocalTime.parse(timeStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:ss a");
    String formatted = time.format(formatter);
    return formatted;
  }

  public static String formatDate (String timeStr) {
    LocalDate date = LocalDate.parse(timeStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d");
    String formatted = date.format(formatter);
    return formatted;
  }

  public static String formatDate (String timeStr, int offSetDays) {
    LocalDate date = LocalDate.parse(timeStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    date = date.plusDays(offSetDays);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d");
    String formatted = date.format(formatter);
    return formatted;
  }
}
