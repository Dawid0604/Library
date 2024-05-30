package pl.tiguarces;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.ZoneId.systemDefault;

public final class AppUtils {
    private AppUtils() { }

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public static LocalDateTime getCurrentDate() {
        return LocalDateTime.now(systemDefault());
    }

    public static String formatDate(final LocalDateTime date) {
        return (date != null) ? DATE_FORMATTER.format(date)
                              : null;
    }
}
