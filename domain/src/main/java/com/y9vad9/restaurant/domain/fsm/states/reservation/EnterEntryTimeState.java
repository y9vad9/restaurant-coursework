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
import com.y9vad9.restaurant.domain.tables.repository.TablesRepository;
import com.y9vad9.restaurant.domain.tables.types.Table;
import com.y9vad9.restaurant.domain.utils.ListUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public record EnterEntryTimeState(Data data) implements BotState<EnterEntryTimeState.Data> {
    @Override
    public CompletableFuture<FSMState<?, IncomingMessage, BotAnswer>> onEnter(IncomingMessage prevIntent, SendActionFunction<BotAnswer> sendAction, FSMContext context) {
        final var strings = context.getElement(Strings.KEY);
        final var tablesRepository = context.getElement(TablesRepository.KEY);
        final var systemRepository = context.getElement(SystemRepository.KEY);
        final var schedule = systemRepository.getSchedule();

        return tablesRepository.getAvailableTime(
            data().guestsNumber(),
            parsed(schedule.from(data().date().getDayOfWeek()))
        ).thenApply(ranges -> {
            final var timeButtons = ListUtils.makeRows(
                ranges
                    .stream()
                    .map(range -> getReservationTimes(range.first(), range.last()))
                    .flatMap(list -> list.stream().map(array -> array[0] + " – " + array[1]))
                    .toList(),
                3
            );

            final var buttonRows = new ArrayList<>(timeButtons);
            buttonRows.add(List.of(strings.getCancelTitle()));

            sendAction.execute(
                new BotAnswer(
                    prevIntent.userId(),
                    strings.getWriteEnterTimeMessage(),
                    buttonRows
                )
            );

            return EnterEntryTimeState.this;
        });
    }

    @Override
    public CompletableFuture<FSMState<?, IncomingMessage, BotAnswer>> onIntent(IncomingMessage message, SendActionFunction<BotAnswer> sendAction, FSMContext context) {
        String input = message.message();
        System.out.println(input);

        Strings strings = context.getElement(Strings.KEY);
        TablesRepository tablesRepository = context.getElement(TablesRepository.KEY);

        if (input.equals(strings.getCancelTitle())) {
            return CompletableFuture.completedFuture(MainMenuState.INSTANCE);
        }

        Range<LocalDateTime> reservationTime;

        try {
            reservationTime = parsed(input);
        } catch (Exception e) {
            sendAction.execute(new BotAnswer(message.userId(), strings.getInvalidInputMessage()));
            onEnter(message, sendAction, context);
            return CompletableFuture.completedFuture(this);
        }

        return tablesRepository.getFreeTables(data().guestsNumber(), reservationTime)
            .exceptionally(throwable -> {
                sendAction.execute(new BotAnswer(message.userId(), strings.getNoTablesAvailableMessage()));
                return null;
            })
            .thenApply(list -> {
                if (!list.isEmpty()) {
                    final var table = new Table(
                        Optional.of(new Table.Reservation(-1, message.userId(), data().name(), data.guestsNumber(), reservationTime)),
                        list.getFirst().number(), data().guestsNumber
                    );

                    tablesRepository.setTableReserved(
                        table.number(),
                        table.reservation().get()
                    ).thenApply(
                        reservationId -> {
                            Table.Reservation oldReservation = table.reservation().get();

                            sendAction.execute(
                                new BotAnswer(
                                    message.userId(),
                                    strings.getSuccessfulBookingMessage(
                                        new Table(
                                            Optional.of(new Table.Reservation(reservationId, oldReservation.userId(), oldReservation.fullName(), oldReservation.reservedSeats(), oldReservation.reservationTime())),
                                            table.number(),
                                            table.seats()
                                        )
                                    )
                                )
                            );
                            return null;
                        }
                    ).join();
                } else {
                    sendAction.execute(new BotAnswer(message.userId(), strings.getNoTablesAvailableMessage()));
                }
                return MainMenuState.INSTANCE;
            });
    }

    private Range<LocalDateTime> parsed(String schedule) {
        String[] split = schedule.trim().split("[-–—]");
        List<String[]> parts = Arrays.stream(split).map(part -> part.split(":")).toList();
        LocalTime startTime = LocalTime.of(Integer.parseInt(parts.get(0)[0].trim()), Integer.parseInt(parts.get(0)[1].trim()));
        LocalTime endTime = LocalTime.of(Integer.parseInt(parts.get(1)[0].trim()), Integer.parseInt(parts.get(1)[1].trim()));
        return new Range<>(data.date().atTime(startTime), data.date().atTime(endTime));
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
