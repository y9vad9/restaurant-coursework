package com.y9vad9.restaurant.domain.system.types;

import java.time.DayOfWeek;

public record Schedule(
    String monday,
    String tuesday,
    String wednesday,
    String thursday,
    String friday,
    String saturday,
    String sunday
) {
    public String from(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> monday;
            case TUESDAY -> tuesday;
            case WEDNESDAY -> wednesday;
            case THURSDAY -> thursday;
            case FRIDAY -> friday;
            case SATURDAY -> saturday;
            case SUNDAY -> sunday;
        };
    }
}
