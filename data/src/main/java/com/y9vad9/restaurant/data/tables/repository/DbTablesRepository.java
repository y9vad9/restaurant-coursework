package com.y9vad9.restaurant.data.TABLES.repository;

import com.y9vad9.restaurant.db.generated.tables.Tables;
import com.y9vad9.restaurant.domain.system.types.Range;
import com.y9vad9.restaurant.domain.system.types.Schedule;
import com.y9vad9.restaurant.domain.system.types.UserId;
import com.y9vad9.restaurant.domain.tables.repository.TablesRepository;
import com.y9vad9.restaurant.domain.tables.types.Table;
import com.y9vad9.restaurant.domain.tables.types.TableOverview;
import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import org.jooq.*;
import org.jooq.impl.DSL;

import static com.y9vad9.restaurant.db.generated.tables.Reservation.RESERVATION;
import static com.y9vad9.restaurant.db.generated.tables.Tables.TABLES;
import static org.jooq.impl.DSL.*;

public class DbTablesRepository implements TablesRepository {
    final DSLContext context;
    final ExecutorService executorService;
    Schedule schedule;

    public DbTablesRepository(DSLContext context, ExecutorService executorService) {
        this.context = context;
        this.executorService = executorService;
    }

    @Override
    public CompletableFuture<Void> addOrFail(Table table) {
        return null;
    }

    @Override
    public Future<List<Table>> getReservedTables() {
        return executorService.submit(
            () -> context.select(TABLES.asterisk(), RESERVATION.asterisk())
                .where(TABLES.reservation().notEqual(null))
                .stream()
                .map(this::mapDbToDomain)
                .toList()
        );
    }

    @Override
    public Future<List<TableOverview>> getFreeTables(LocalDate date, Range<LocalTime> timeRange) {
        return executorService.submit(
            () -> context.select(TABLES.asterisk(), RESERVATION.asterisk())
                .where(TABLES.reservation().eq(null))
                .stream()
                .map(this::mapDbToDomain)
                .toList()
        );
    }

    @Override
    public CompletableFuture<List<Range<LocalTime>>> getAvailableTime(int guestsNumber, Range<LocalTime> possibleTime) {
        CompletableFuture.supplyAsync(
            () -> context.select(count(RESERVATION.TIME_END.minus(RESERVATION.TIME_START)).as("r_sum"), TABLES.NUMBER)
                    .from(TABLES)
                    .leftJoin(RESERVATION)
                    .on(RESERVATION.TABLE_ID.eq(TABLES.NUMBER))
                    .where(TABLES.SEATS.greaterOrEqual(guestsNumber))
                    .orderBy(field("r_sum"))
                    .fetchAny()
            ,
            executorService
        ).thenApply(record -> {
            context.select(RESERVATION.asterisk())
                .from(RESERVATION)
                .where(RESERVATION.TABLE_ID.eq(record.get(TABLES.NUMBER)).and(RESERVATION.TIME_START.greaterOrEqual(possibleTime.first())))
            if ()
        });




        return context.select(select).fetchAsync().thenApply(result -> result.into(Range.class));
    }

    private Table mapDbToDomain(Record record) {
        Table.Reservation reservation = null;
        boolean isReserved = record.field(RESERVATION.GUEST_NAME) != null;

        if (isReserved) {
            reservation = new Table.Reservation(
                UserId.fromString(record.get(RESERVATION.GUEST_ID)),
                record.get(RESERVATION.GUEST_NAME),
                new Range<>(record.get(RESERVATION.TIME_START).toLocalTime(), record.get(RESERVATION.TIME_END).toLocalTime())
            );
        }

        return new Table(Optional.ofNullable(reservation), record.get(TABLES.NUMBER), isReserved ? record.get(RESERVATION.RESERVED_SEATS) : record.get(TABLES.SEATS));
    }
}
