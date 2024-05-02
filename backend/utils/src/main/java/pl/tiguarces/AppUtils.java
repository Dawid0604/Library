package pl.tiguarces;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class AppUtils {
    private AppUtils() { }

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy H:mm");

    public static String formatDate(final LocalDateTime date) {
        return (date != null) ? DATE_FORMATTER.format(date)
                              : null;
    }
}
