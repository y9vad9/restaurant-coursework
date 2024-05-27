package com.y9vad9.restaurant.domain.fsm.states.reservation;

import com.y9vad9.jfsm.FSMState;
import com.y9vad9.jfsm.context.FSMContext;
import com.y9vad9.jfsm.functions.SendActionFunction;
import com.y9vad9.restaurant.domain.fsm.BotAnswer;
import com.y9vad9.restaurant.domain.fsm.BotState;
import com.y9vad9.restaurant.domain.fsm.IncomingMessage;
import com.y9vad9.restaurant.domain.fsm.states.MainMenuState;
import com.y9vad9.restaurant.domain.system.strings.Strings;

import java.util.concurrent.CompletableFuture;

public class EnterYourNameState implements BotState<Void> {
    public static EnterYourNameState INSTANCE = new EnterYourNameState();

    @Override
    public CompletableFuture<FSMState<?, IncomingMessage, BotAnswer>> onEnter(IncomingMessage prevIntent, SendActionFunction<BotAnswer> sendAction, FSMContext context) {
        final var strings = context.getElement(Strings.KEY);

        sendAction.execute(
            new BotAnswer(
                prevIntent.userId(),
                strings.getWriteNameForBookingMessage(),
                strings.getCancelTitle()
            )
        );
        return CompletableFuture.completedFuture(this);
    }

    @Override
    public Void data() {
        return null;
    }

    @Override
    public CompletableFuture<FSMState<?, IncomingMessage, BotAnswer>> onIntent(
        IncomingMessage message,
        SendActionFunction<BotAnswer> sendAction,
        FSMContext context
    ) {
        String input = message.message();
        Strings strings = context.getElement(Strings.KEY);

        if (input.equals(strings.getCancelTitle())) {
            return CompletableFuture.completedFuture(MainMenuState.INSTANCE);
        }

        return CompletableFuture.completedFuture(
            new EnterGuestsNumberState(new EnterGuestsNumberState.Data(input))
        );
    }
}
