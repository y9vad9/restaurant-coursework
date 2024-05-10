package com.y9vad9.restaurant.domain.fsm.states.reservation;

import com.y9vad9.jfsm.FSMState;
import com.y9vad9.jfsm.context.FSMContext;
import com.y9vad9.jfsm.functions.SendActionFunction;
import com.y9vad9.restaurant.domain.fsm.BotAnswer;
import com.y9vad9.restaurant.domain.fsm.BotState;
import com.y9vad9.restaurant.domain.fsm.IncomingMessage;
import com.y9vad9.restaurant.domain.fsm.states.MainMenuState;
import com.y9vad9.restaurant.domain.system.repositories.SystemRepository;
import com.y9vad9.restaurant.domain.system.strings.Strings;
import com.y9vad9.restaurant.domain.system.types.Range;
import com.y9vad9.restaurant.domain.system.types.Schedule;
import com.y9vad9.restaurant.domain.tables.repository.TablesRepository;
import com.y9vad9.restaurant.domain.tables.types.Table;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public record EnterEntryTimeState(Data data) implements BotState<EnterEntryTimeState.Data> {
    @Override
    public CompletableFuture<FSMState<?, IncomingMessage, BotAnswer>> onEnter(IncomingMessage prevIntent, SendActionFunction<BotAnswer> sendAction, FSMContext context) {
        Strings strings = context.getElement(Strings.KEY);
        TablesRepository tablesRepository = context.getElement(TablesRepository.KEY);
        SystemRepository systemRepository = context.getElement(SystemRepository.KEY);
        Schedule schedule = systemRepository.getSchedule();

        return tablesRepository.getAvailableTime(
            data().guestsNumber(),
            parsed(schedule.from(LocalDate.now(systemRepository.getZoneId()).getDayOfWeek()))
        ).thenApply(ranges -> {
            List<String> buttons = ranges
                .stream()
                .map(range -> getReservationTimes(range.first(), range.last()))
                .flatMap(list -> list.stream().map(array -> array[0] + " – " + array[1]))
                .toList();

            sendAction.execute(
                new BotAnswer(
                    prevIntent.userId(),
                    strings.getWriteEnterTimeMessage(),
                    List.of(buttons, List.of(strings.getCancelTitle()))
                )
            );

            return EnterEntryTimeState.this;
        });
    }

    @Override
    public CompletableFuture<FSMState<?, IncomingMessage, BotAnswer>> onIntent(IncomingMessage message, SendActionFunction<BotAnswer> sendAction, FSMContext context) {
        String input = message.message();

        Strings strings = context.getElement(Strings.KEY);
        TablesRepository tablesRepository = context.getElement(TablesRepository.KEY);
        SystemRepository systemRepository = context.getElement(SystemRepository.KEY);
        Schedule schedule = systemRepository.getSchedule();

        if (input.equals(strings.getCancelTitle())) {
            return CompletableFuture.completedFuture(MainMenuState.INSTANCE);
        }

        Range<LocalTime> reservationTime;

        try {
            reservationTime = parsed(input);
        } catch (Exception e) {
            sendAction.execute(new BotAnswer(message.userId(), strings.getInvalidInputMessage()));
            onEnter(message, sendAction, context);
            return CompletableFuture.completedFuture(this);
        }

        // TODO finish validation and saving
        tablesRepository.getFreeTables(data().date, reservationTime)
            .thenApply(list -> {
                if (!list.isEmpty()) {
                    Table table = new Table(
                        Optional.of(new Table.Reservation(message.userId(), data().name, reservationTime)),
                        list.getFirst().number(), data().guestsNumber
                    );
                    tablesRepository.addOrFail(
                        table
                    );

                    sendAction.execute(
                        new BotAnswer(
                            message.userId(),
                            strings.getSuccessfulBookingMessage(table)
                        )
                    );

                    return MainMenuState.INSTANCE;
                } else {
                    return this;
                }
            });

        return CompletableFuture.completedFuture(MainMenuState.INSTANCE);
    }

    private Range<LocalTime> parsed(String schedule) {
        String[] split = schedule.trim().split("[-–—]");
        List<String[]> parts = Arrays.stream(split).map(part -> part.split(":")).toList();
        LocalTime startTime = LocalTime.of(Integer.parseInt(parts.get(0)[0]), Integer.parseInt(parts.get(0)[1]));
        LocalTime endTime = LocalTime.of(Integer.parseInt(parts.get(1)[0]), Integer.parseInt(parts.get(1)[1]));
        return new Range<>(startTime, endTime);
    }

    private List<String[]> getReservationTimes(LocalTime beginTime, LocalTime endTime) {
        List<String[]> reservationTimes = new ArrayList<>();

        LocalTime currentTime = beginTime;
        while (currentTime.isBefore(endTime) || currentTime.equals(endTime)) {
            LocalTime fromTime = currentTime;
            LocalTime toTime = currentTime.plusHours(1);
            if (toTime.isAfter(endTime)) {
                break;
            }
            reservationTimes.add(new String[]{fromTime.toString(), toTime.toString()});
            currentTime = toTime;
        }

        return reservationTimes;
    }

    record Data(String name, int guestsNumber, LocalDate date) {
    }
}
