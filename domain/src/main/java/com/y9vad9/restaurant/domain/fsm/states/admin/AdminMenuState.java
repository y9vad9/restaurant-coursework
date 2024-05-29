package com.y9vad9.restaurant.domain.fsm.states.admin;

import com.y9vad9.jfsm.FSMState;
import com.y9vad9.jfsm.context.FSMContext;
import com.y9vad9.jfsm.functions.SendActionFunction;
import com.y9vad9.restaurant.domain.fsm.BotAnswer;
import com.y9vad9.restaurant.domain.fsm.BotState;
import com.y9vad9.restaurant.domain.fsm.IncomingMessage;
import com.y9vad9.restaurant.domain.fsm.states.reservation.EnterYourNameState;
import com.y9vad9.restaurant.domain.system.strings.Strings;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public record AdminMenuState(Data data) implements BotState<AdminMenuState.Data> {
    public static final AdminMenuState INSTANCE = new AdminMenuState(new Data(Optional.empty()));

    public record Data(Optional<Strings> localeOverride) {
    }

    @Override
    public CompletableFuture<FSMState<?, IncomingMessage, BotAnswer>> onEnter(
        IncomingMessage prevIntent,
        SendActionFunction<BotAnswer> sendAction,
        FSMContext context
    ) {
        Strings strings = data().localeOverride().orElseGet(() -> context.getElement(Strings.KEY));

        sendAction.execute(
            new BotAnswer(
                prevIntent.userId(),
                strings.getAdminHelloMessage(),
                List.of(List.of(strings.getBookTableTitle(), strings.getReservationsListAdmin()))
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
        Strings strings = data().localeOverride.orElseGet(() -> context.getElement(Strings.KEY));
        String text = message.message();

        if (text.equals(strings.getBookTableTitle())) {
            return CompletableFuture.completedFuture(EnterYourNameState.INSTANCE);
        } else if (text.equals(strings.getReservationsListAdmin())) {
            return CompletableFuture.completedFuture(AdminReservationsListState.INSTANCE);
        } else {
            sendAction.execute(new BotAnswer(message.userId(), strings.getUnknownCommandMessage()));
            return CompletableFuture.completedFuture(this);
        }
    }
}
