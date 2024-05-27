package com.y9vad9.restaurant.domain.tables.repository;

import com.y9vad9.jfsm.context.FSMContextElement;
import com.y9vad9.restaurant.domain.system.types.Range;
import com.y9vad9.restaurant.domain.system.types.UserId;
import com.y9vad9.restaurant.domain.tables.types.Table;
import com.y9vad9.restaurant.domain.tables.types.TableOverview;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * Інтерфейс, який визначає методи для роботи з репозиторієм столів.
 */
public interface TablesRepository extends FSMContextElement {
    FSMContextElement.Key<TablesRepository> KEY = Key.create();

    @Override
    default Key<?> key() {
        return KEY;
    }

    CompletableFuture<Integer> setTableReserved(int tableNumber, Table.Reservation reservation);

    CompletableFuture<List<Table>> getReservedTables(UserId userId);

    CompletableFuture<List<Table>> getReservedTables(Range<LocalDateTime> timeRange);

    CompletableFuture<Void> removeReservation(int reservationId);

    CompletableFuture<Optional<Table.Reservation>> getReservation(int reservationId);

    CompletableFuture<List<TableOverview>> getFreeTables(int guests, Range<LocalDateTime> timeRange);

    CompletableFuture<List<Range<LocalTime>>> getAvailableTime(int guestsNumber, Range<LocalDateTime> possibleTime);

    CompletableFuture<Integer> getMaxTableCapacity();
}

