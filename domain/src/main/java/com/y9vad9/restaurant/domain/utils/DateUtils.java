package com.y9vad9.restaurant.domain.utils;

import com.y9vad9.restaurant.domain.system.strings.Strings;
import com.y9vad9.restaurant.domain.system.types.Schedule;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;

public final class DateUtils {
    private DateUtils() {}

    public static final String DATE_REGEX = "^(0[1-9]|[12][0-9]|3[01])\\.(0[1-9]|1[0-2])\\.\\d{4}$";

    public static List<String> getFollowingWorkingDays(int count, Strings strings, ZoneId zoneId, Schedule schedule) {
        List<String> daysList = new ArrayList<>();
        LocalDate currentDate = LocalDate.now(zoneId).plusDays(1); // Get the date starting from tomorrow in UTC

        for (int i = 0; i < count; i++) {
            String scheduleForDay = schedule.from(currentDate.getDayOfWeek());
            if (scheduleForDay != null && !scheduleForDay.isEmpty()) {
                String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                String displayName = currentDate.getDayOfWeek().getDisplayName(TextStyle.FULL, strings.getLocale());
                String dayWithDisplayName = formattedDate + " (" + displayName + ")";
                daysList.add(dayWithDisplayName);
            }
            currentDate = currentDate.plusDays(1); // Move to the next day
        }

        return daysList;
    }

    @Nullable
    public static LocalDate parseDate(String input) {
        String[] parts = input.split("\\.");

        try {
            return LocalDate.of(
                Integer.parseInt(parts[2]),
                Integer.parseInt(parts[1]),
                Integer.parseInt(parts[0])
            );
        } catch (Exception exception) {
            return null;
        }
    }
}
