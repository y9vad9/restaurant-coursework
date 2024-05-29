package com.y9vad9.restaurant.initializers;

import com.y9vad9.restaurant.cli.Arguments;
import com.y9vad9.restaurant.entities.Config;
import com.y9vad9.restaurant.entities.TableCapacity;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static com.y9vad9.restaurant.db.generated.tables.Locales.LOCALES;
import static com.y9vad9.restaurant.db.generated.tables.Reservation.RESERVATION;
import static com.y9vad9.restaurant.db.generated.tables.States.STATES;
import static com.y9vad9.restaurant.db.generated.tables.Tables.TABLES;

public final class DatabaseInitializer {
    public static DSLContext initializeDatabase(Arguments arguments, Config config) throws SQLException {
        String databaseUrl = arguments.getNamed("databaseUrl").orElseGet(() -> System.getenv("DATABASE_URL"));
        String databaseUser = arguments.getNamed("databaseUser").orElseGet(() -> System.getenv("DATABASE_USER"));
        String databasePassword = arguments.getNamed("databasePassword").orElseGet(() -> System.getenv("DATABASE_PASSWORD"));

        DSLContext dslContext = DSL.using(DriverManager.getConnection(databaseUrl, databaseUser, databasePassword));
        dslContext.settings();
        createTables(dslContext);
        createTableEntitiesFromConfig(dslContext, config.tableCapacities());

        return dslContext;
    }

    private static void createTables(DSLContext dslContext) {
        dslContext.createTableIfNotExists(TABLES)
            .column(TABLES.NUMBER)
            .column(TABLES.SEATS)
            .execute();

        dslContext.createTableIfNotExists(RESERVATION)
            .column(RESERVATION.GUEST)
            .column(RESERVATION.GUEST_NAME)
            .column(RESERVATION.RESERVED_SEATS)
            .column(RESERVATION.TIME_START)
            .column(RESERVATION.TIME_END)
            .column(RESERVATION.TABLE_ID)
            .column(RESERVATION.ID)
            .execute();

        dslContext.createTableIfNotExists(LOCALES)
            .column(LOCALES.USER_ID)
            .column(LOCALES.LANG_CODE)
            .execute();

        dslContext.createTableIfNotExists(STATES)
            .column(STATES.USER_ID)
            .column(STATES.JSON_VALUE)
            .execute();
    }

    private static void createTableEntitiesFromConfig(DSLContext context, List<TableCapacity> capacities) {
        final var queries = capacities.stream().map(
            capacity -> context.insertInto(TABLES, TABLES.NUMBER, TABLES.SEATS)
                .values(capacity.number(), capacity.availableSeats())
                .onDuplicateKeyUpdate()
                .set(TABLES.SEATS, capacity.availableSeats())
        ).toList();

        context.batch(queries).execute();

        context.deleteFrom(TABLES)
            .where(TABLES.NUMBER.notIn(capacities.stream().map(TableCapacity::number).toList()))
            .execute();
    }
}
