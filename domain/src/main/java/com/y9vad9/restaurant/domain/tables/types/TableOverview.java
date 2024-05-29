package com.y9vad9.restaurant.domain.tables.types;

import com.y9vad9.restaurant.domain.system.types.Range;

import java.time.LocalTime;
import java.util.List;

public record TableOverview(
    int number,
    int availableSeats,
    List<Range<LocalTime>> time
) {
}
