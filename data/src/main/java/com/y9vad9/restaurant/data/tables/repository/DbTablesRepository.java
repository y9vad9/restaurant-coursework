package com.y9vad9.restaurant.data.tables.repository;

import com.y9vad9.restaurant.domain.system.types.Range;
import com.y9vad9.restaurant.domain.system.types.UserId;
import com.y9vad9.restaurant.domain.tables.repository.TablesRepository;
import com.y9vad9.restaurant.domain.tables.types.Table;
import com.y9vad9.restaurant.domain.tables.types.TableOverview;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.impl.DSL;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static com.y9vad9.restaurant.db.generated.tables.Reservation.RESERVATION;
import static com.y9vad9.restaurant.db.generated.tables.Tables.TABLES;

public class DbTablesRepository implements TablesRepository {
    final DSLContext context;
    final ExecutorService executorService;

    public DbTablesRepository(DSLContext context, ExecutorService executorService) {
        this.context = context;
        this.executorService = executorService;
    }

    @Override
    public CompletableFuture<Integer> setTableReserved(int tableNumber, Table.Reservation reservation) {
        return CompletableFuture.supplyAsync(
            () -> context.insertInto(RESERVATION, RESERVATION.GUEST, RESERVATION.GUEST_NAME, RESERVATION.RESERVED_SEATS, RESERVATION.TIME_START, RESERVATION.TIME_END, RESERVATION.TABLE_ID)
                .values(reservation.userId().toString(), reservation.fullName(), reservation.reservedSeats(), reservation.reservationTime().first(), reservation.reservationTime().last(), tableNumber)
                .returning(RESERVATION.ID)
                .fetchOne(RESERVATION.ID)
        );
    }

    @Override
    public CompletableFuture<List<Table>> getReservedTables(UserId userId) {
        return CompletableFuture.supplyAsync(
            () -> context.select(DSL.asterisk())
                .from(RESERVATION)
                .join(TABLES)
                .on(TABLES.NUMBER.eq(RESERVATION.TABLE_ID))
                .where(RESERVATION.GUEST.eq(userId.toString()))
                .stream()
                .map(this::mapDbToDomain)
                .toList(),
            executorService
        );
    }

    @Override
    public CompletableFuture<List<Table>> getReservedTables(Range<LocalDateTime> timeRange) {
        return CompletableFuture.supplyAsync(
            () -> context.select(DSL.asterisk())
                .from(RESERVATION)
                .join(TABLES)
                .on(TABLES.NUMBER.eq(RESERVATION.TABLE_ID))
                .where(
                    RESERVATION.TIME_START.greaterOrEqual(timeRange.first())
                        .and(RESERVATION.TIME_END.lessOrEqual(timeRange.last()))
                        .or(RESERVATION.TIME_START.between(timeRange.first(), timeRange.last()))
                        .or(RESERVATION.TIME_END.between(timeRange.first(), timeRange.last()))
                )
                .stream()
                .map(this::mapDbToDomain)
                .toList(),
            executorService
        );
    }

    @Override
    public CompletableFuture<Void> removeReservation(int reservationId) {
        return CompletableFuture.supplyAsync(
            () -> {
                context.deleteFrom(RESERVATION).where(RESERVATION.ID.eq(reservationId)).execute();
                return null;
            },
            executorService
        );
    }

    @Override
    public CompletableFuture<Optional<Table.Reservation>> getReservation(int reservationId) {
        return CompletableFuture.supplyAsync(
            () -> context.selectFrom(RESERVATION)
                .where(RESERVATION.ID.eq(reservationId))
                .fetchOptional()
                .map(this::toDomainReservation)
        );
    }

    @Override
    public CompletableFuture<List<TableOverview>> getFreeTables(int guests, Range<java.time.LocalDateTime> timeRange) {
        return CompletableFuture.supplyAsync(
            () -> context.select(TABLES.asterisk())
                .from(TABLES)
                .whereNotExists(
                    context.selectOne()
                        .from(RESERVATION)
                        .where(
                            RESERVATION.TABLE_ID.eq(TABLES.NUMBER).and(
                                RESERVATION.TIME_START.lessThan(timeRange.last())
                                    .and(RESERVATION.TIME_END.greaterThan(timeRange.first()))
                                    .or(RESERVATION.TIME_START.between(timeRange.first(), timeRange.last()))
                                    .or(RESERVATION.TIME_END.between(timeRange.first(), timeRange.last())))
                        )
                )
                .and(TABLES.SEATS.greaterOrEqual(guests))
                .stream()
                .map(this::mapDbToDomainOverview)
                .toList(),
            executorService
        );
    }

    @Override
    public CompletableFuture<List<Range<LocalTime>>> getAvailableTime(int guestsNumber, Range<LocalDateTime> possibleTime) {
        return CompletableFuture.supplyAsync(() -> {
            long availableTables = context.selectCount()
                .from(TABLES)
                .where(TABLES.SEATS.greaterOrEqual(guestsNumber))
                .fetchSingleInto(Long.class);

            List<Range<LocalTime>> initialTimeRanges = createTimeRanges(new Range<>(possibleTime.first().toLocalTime(), possibleTime.last().toLocalTime()));

            Result<Record3<Integer, LocalDateTime, LocalDateTime>> overlappingReservations = context
                .select(RESERVATION.TABLE_ID, RESERVATION.TIME_START, RESERVATION.TIME_END)
                .from(RESERVATION)
                .where(RESERVATION.TIME_START.lessThan(possibleTime.last()))
                .and(RESERVATION.TIME_END.greaterThan(possibleTime.first()))
                .fetch();

            HashMap<LocalTime, Integer> occupiedRanges = new HashMap<>();

            for (Record3<Integer, LocalDateTime, LocalDateTime> reservation : overlappingReservations) {
                LocalTime resStart = reservation.get(RESERVATION.TIME_START).toLocalTime();
                LocalTime resEnd = reservation.get(RESERVATION.TIME_END).toLocalTime();

                for (LocalTime hour = resStart; hour.isBefore(resEnd); hour = hour.plusHours(1)) {
                    occupiedRanges.put(hour, occupiedRanges.getOrDefault(hour, 0) + 1);
                }
            }

            List<Range<LocalTime>> finalAvailableRanges = initialTimeRanges.stream()
                .filter(hourRange -> {
                    int occupancyCount = 0;
                    for (LocalTime hour = hourRange.first(); hour.isBefore(hourRange.last()); hour = hour.plusHours(1)) {
                        occupancyCount += occupiedRanges.getOrDefault(hour, 0);
                    }
                    return occupancyCount <= availableTables;
                })
                .toList();

            return finalAvailableRanges;
        }, executorService);
    }


    private List<Range<LocalTime>> createTimeRanges(Range<LocalTime> possibleTime) {
        List<Range<LocalTime>> timeRanges = new ArrayList<>();
        LocalTime start = possibleTime.first();
        LocalTime end = possibleTime.last();

        while (start.isBefore(end)) {
            LocalTime nextHour = start.plusHours(1);
            LocalTime rangeEnd = nextHour.isBefore(end) ? nextHour : end;
            timeRanges.add(new Range<>(start, rangeEnd));
            start = nextHour;
        }

        return timeRanges;
    }


    @Override
    public CompletableFuture<Integer> getMaxTableCapacity() {
        return CompletableFuture.supplyAsync(
            () ->
                Objects.requireNonNull(
                    context.select(DSL.max(TABLES.SEATS).as("max_value"))
                        .from(TABLES).fetchAny()
                ).get(DSL.field("max_value", Integer.class)),
            executorService
        );
    }

    private Table mapDbToDomain(Record record) {
        Table.Reservation reservation = null;
        boolean isReserved = record.field(RESERVATION.GUEST_NAME) != null;

        if (isReserved) {
            reservation = toDomainReservation(record);
        }

        return new Table(Optional.ofNullable(reservation), record.get(TABLES.NUMBER), isReserved ? record.get(RESERVATION.RESERVED_SEATS) : record.get(TABLES.SEATS));
    }

    private Table.Reservation toDomainReservation(Record record) {
        return new Table.Reservation(
            record.get(RESERVATION.ID),
            UserId.fromString(record.get(RESERVATION.GUEST)),
            record.get(RESERVATION.GUEST_NAME),
            record.get(RESERVATION.RESERVED_SEATS),
            new Range<>(record.get(RESERVATION.TIME_START), record.get(RESERVATION.TIME_END))
        );
    }

    private TableOverview mapDbToDomainOverview(Record record) {
        return new TableOverview(
            record.get(TABLES.NUMBER),
            record.get(TABLES.SEATS),
            null
        );
    }
}
