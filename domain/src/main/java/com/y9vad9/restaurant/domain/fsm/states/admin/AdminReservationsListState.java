package com.y9vad9.restaurant.domain.fsm.states.admin;

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
import com.y9vad9.restaurant.domain.system.types.UserId;
import com.y9vad9.restaurant.domain.tables.repository.TablesRepository;
import com.y9vad9.restaurant.domain.utils.DateUtils;
import com.y9vad9.restaurant.domain.utils.ListUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record AdminReservationsListState(Void data) implements BotState<Void> {
    public static final AdminReservationsListState INSTANCE = new AdminReservationsListState(null);

    private static final String CANCEL_COMMAND_PATTERN = "\\/cancel\\s(\\d*)\\s(.*)";

    @Override
    public CompletableFuture<FSMState<?, IncomingMessage, BotAnswer>> onEnter(IncomingMessage prevIntent, SendActionFunction<BotAnswer> sendAction, FSMContext context) {
        ZoneId zoneId = context.getElement(SystemRepository.KEY).getZoneId();
        Strings strings = context.getElement(Strings.KEY);
        Schedule schedule = context.getElement(SystemRepository.KEY).getSchedule();

        List<List<String>> rows = ListUtils.makeRows(
            DateUtils.getFollowingWorkingDays(14, strings, zoneId, schedule),
            2
        );
        rows.addFirst(List.of(strings.getTodayTitle()));
        rows.add(List.of(strings.getCancelTitle()));

        sendAction.execute(
            new BotAnswer(
                prevIntent.userId(),
                strings.getSelectDateMessage(),
                rows
            )
        );
        return CompletableFuture.completedFuture(this);
    }

    @Override
    public CompletableFuture<FSMState<?, IncomingMessage, BotAnswer>> onIntent(
        IncomingMessage message,
        SendActionFunction<BotAnswer> sendAction,
        FSMContext context
    ) {
        final var zoneId = context.getElement(SystemRepository.KEY).getZoneId();

        final var strings = context.getElement(Strings.KEY);
        final var tables = context.getElement(TablesRepository.KEY);
        final var text = message.message();

        if (text.equals(strings.getCancelTitle())) {
            return CompletableFuture.completedFuture(MainMenuState.INSTANCE);
        } else if (text.equals(strings.getTodayTitle())) {
            LocalDate localDate = LocalDate.now(zoneId);
            return getReservedAndSendMessage(localDate, strings, tables, sendAction, message.userId());
        } else if (text.matches(CANCEL_COMMAND_PATTERN)) {
            Matcher matcher = Pattern.compile(CANCEL_COMMAND_PATTERN).matcher(text);

            int reservationId = -1;
            String reason = null;


            while (matcher.find()) {
                reservationId = Integer.parseInt(matcher.group(1));
                reason = matcher.group(2);
            }

            sendAction.execute(
                new BotAnswer(
                    message.userId(),
                    strings.getReservationCanceledRegular()
                )
            );

            String finalReason = reason;
            int finalReservationId = reservationId;
            return tables.getReservation(reservationId).thenApply(
                result -> {
                    result.ifPresent(reservation -> {
                        sendAction.execute(
                            new BotAnswer(
                                reservation.userId(),
                                strings.getReservationCanceled(reservation, finalReason)
                            )
                        );

                        tables.removeReservation(finalReservationId);
                    });

                    return AdminReservationsListState.this;
                }
            );
        } else {
            String dateStr = text.substring(0, text.indexOf("(")).trim();

            if (dateStr.matches(DateUtils.DATE_REGEX)) {
                LocalDate localDate = DateUtils.parseDate(dateStr);

                if (localDate == null) {
                    sendAction.execute(new BotAnswer(message.userId(), strings.getInvalidInputMessage()));
                    return CompletableFuture.completedFuture(this);
                }

                return getReservedAndSendMessage(localDate, strings, tables, sendAction, message.userId());
            } else {
                sendAction.execute(new BotAnswer(message.userId(), strings.getUnknownCommandMessage()));
                onEnter(message, sendAction, context);
                return CompletableFuture.completedFuture(this);
            }
        }
    }

    private CompletableFuture<FSMState<?, IncomingMessage, BotAnswer>> getReservedAndSendMessage(
        LocalDate localDate,
        Strings strings,
        TablesRepository tables,
        SendActionFunction<BotAnswer> sendAction,
        UserId userId
    ) {
        return tables.getReservedTables(new Range<>(localDate.atTime(0, 0), localDate.atTime(23, 0)))
            .thenApply(
                value -> {
                    sendAction.execute(
                        new BotAnswer(userId, strings.getReservationsListAdminMessage(value))
                    );
                    return AdminReservationsListState.this;
                }
            );
    }
}
