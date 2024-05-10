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
import com.y9vad9.restaurant.domain.system.types.Schedule;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public record EnterEntryDateState(Data data) implements BotState<EnterEntryDateState.Data> {
    record Data(String name, int guestsNumber) {
    }

    private static final String DATE_REGEX = "^(0[1-9]|[12][0-9]|3[01])\\.(0[1-9]|1[0-2])\\.\\d{4}$\n";

    @Override
    public CompletableFuture<FSMState<?, IncomingMessage, BotAnswer>> onEnter(IncomingMessage message, SendActionFunction<BotAnswer> sendAction, FSMContext context) {
        ZoneId zoneId = context.getElement(SystemRepository.KEY).getZoneId();
        Strings strings = context.getElement(Strings.KEY);

        List<List<String>> rows = createRows(getNext14Days(strings, zoneId), 2);
        rows.add(List.of(strings.getCancelTitle()));

        sendAction.execute(
            new BotAnswer(message.userId(), strings.getWriteDateOfEntryMessage(), rows)
        );

        return CompletableFuture.completedFuture(this);
    }

    @Override
    public CompletableFuture<FSMState<?, IncomingMessage, BotAnswer>> onIntent(
        IncomingMessage message,
        SendActionFunction<BotAnswer> sendAction,
        FSMContext context
    ) {
        String input = message.message();
        Strings strings = context.getElement(Strings.KEY);
        Schedule schedule = context
            .getElement(SystemRepository.KEY)
            .getSchedule();

        if (input.equals(strings.getCancelTitle())) {
            return CompletableFuture.completedFuture(MainMenuState.INSTANCE);
        }

        if (input.matches(DATE_REGEX)) {
            String[] parts = input.split("\\.");

            LocalDate localDate;

            try {
                localDate = LocalDate.of(
                    Integer.parseInt(parts[2]),
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[0])
                );
            } catch (Exception e) {
                sendAction.execute(new BotAnswer(message.userId(), strings.getInvalidInputMessage()));
                return CompletableFuture.completedFuture(this);
            }

            String scheduleOfDay = schedule.from(localDate.getDayOfWeek());
            if (scheduleOfDay == null || scheduleOfDay.matches("[-–—]")) {
                sendAction.execute(new BotAnswer(message.userId(), strings.getWeDontWorkAtGivenDay()));
                return onEnter(message, sendAction, context);
            }

            return CompletableFuture.completedFuture(
                new EnterEntryTimeState(
                    new EnterEntryTimeState.Data(data().name, data().guestsNumber(), localDate)
                )
            );
        } else {
            sendAction.execute(new BotAnswer(message.userId(), strings.getInvalidInputMessage()));
            onEnter(message, sendAction, context);

            return CompletableFuture.completedFuture(
                this
            );
        }
    }

    private List<String> getNext14Days(Strings strings, ZoneId zoneId) {
        List<String> daysList = new ArrayList<>();
        LocalDate currentDate = LocalDate.now(zoneId).plusDays(1); // Get the date starting from tomorrow in UTC

        for (int i = 0; i < 14; i++) {
            String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            String displayName = currentDate.getDayOfWeek().getDisplayName(TextStyle.FULL, strings.getLocale());
            String dayWithDisplayName = formattedDate + " (" + displayName + ")";
            daysList.add(dayWithDisplayName);
            currentDate = currentDate.plusDays(1); // Move to the next day
        }

        return daysList;
    }

    private List<List<String>> createRows(List<String> list, int elementsPerRow) {
        List<List<String>> rows = new ArrayList<>();
        for (int i = 0; i < list.size(); i += elementsPerRow) {
            int endIndex = Math.min(i + elementsPerRow, list.size());
            rows.add(list.subList(i, endIndex));
        }
        return rows;
    }
}
