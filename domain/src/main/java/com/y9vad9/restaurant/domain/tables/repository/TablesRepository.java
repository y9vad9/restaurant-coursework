package com.y9vad9.restaurant.domain.tables.repository;

import com.y9vad9.jfsm.context.FSMContextElement;
import com.y9vad9.restaurant.domain.system.types.Range;
import com.y9vad9.restaurant.domain.tables.types.Table;
import com.y9vad9.restaurant.domain.tables.types.TableOverview;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
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

    /**
     * Додає або замінює існуючий стіл.
     *
     * @param table стіл для додавання або заміни
     * @return об'єкт {@link CompletableFuture}, який містить {@code null}, якщо операція пройшла успішно
     */
    Future<Void> addOrFail(Table table);

    /**
     * Отримує список заброньованих столів з репозиторію.
     *
     * @return об'єкт {@link Future}, який містить список заброньованих столів
     */
    Future<List<Table>> getReservedTables();

    /**
     * Отримує список вільних столів з репозиторію.
     *
     * @return об'єкт {@link Future}, який містить список вільних столів
     */
    Future<List<TableOverview>> getFreeTables(LocalDate date, Range<LocalTime> timeRange);

    Future<List<Range<LocalTime>>> getAvailableTime(int guestsNumber, Range<LocalDateTime> possibleTime);
}

