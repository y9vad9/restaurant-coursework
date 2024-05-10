package com.y9vad9.restaurant.domain.tables.types;

import com.y9vad9.restaurant.domain.system.types.Range;
import com.y9vad9.restaurant.domain.system.types.UserId;

import java.time.LocalTime;
import java.util.Optional;

/**
 * Запис, що представляє стіл з данними про нього.
 *
 * @param reservation вказує на резервацію, якщо присутня.
 * @param number      вказує на номер столика, який відповідає цьому екземпляру.
 */
public record Table(
    Optional<Reservation> reservation,
    int number,
    int seats
) {
    public record Reservation(UserId userId, String fullName, Range<LocalTime> reservationTime) {
    }
}
