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
import com.y9vad9.restaurant.domain.utils.DateUtils;
import com.y9vad9.restaurant.domain.utils.ListUtils;

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

    @Override
    public CompletableFuture<FSMState<?, IncomingMessage, BotAnswer>> onEnter(IncomingMessage message, SendActionFunction<BotAnswer> sendAction, FSMContext context) {
        ZoneId zoneId = context.getElement(SystemRepository.KEY).getZoneId();
        Strings strings = context.getElement(Strings.KEY);
        Schedule schedule = context.getElement(SystemRepository.KEY).getSchedule();

        List<List<String>> rows = ListUtils.makeRows(DateUtils.getFollowingWorkingDays(14, strings, zoneId, schedule), 2);
        rows.add(List.of(strings.getCancelTitle()));

        if (rows.size() > 1) {
            sendAction.execute(
                new BotAnswer(message.userId(), strings.getWriteDateOfEntryMessage(), rows)
            );
        } else {
            sendAction.execute(
                new BotAnswer(message.userId(), strings.getNoAvailableDays())
            );
            return CompletableFuture.completedFuture(MainMenuState.INSTANCE);
        }

        return CompletableFuture.completedFuture(this);
    }

    @Override
    public CompletableFuture<FSMState<?, IncomingMessage, BotAnswer>> onIntent(
        IncomingMessage message,
        SendActionFunction<BotAnswer> sendAction,
        FSMContext context
    ) {
        String input = message.message().substring(0, message.message().indexOf("(")).trim();
        Strings strings = context.getElement(Strings.KEY);
        Schedule schedule = context
            .getElement(SystemRepository.KEY)
            .getSchedule();

        if (input.equals(strings.getCancelTitle())) {
            return CompletableFuture.completedFuture(MainMenuState.INSTANCE);
        }

        if (input.matches(DateUtils.DATE_REGEX)) {
            LocalDate localDate = DateUtils.parseDate(input);

            if (localDate == null) {
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
}
