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

public record EnterGuestsNumberState(Data data) implements BotState<EnterGuestsNumberState.Data> {

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

        int guestsNumber;

        try {
            guestsNumber = Integer.parseInt(input);
        } catch (Exception e) {
            sendAction.execute(
                new BotAnswer(message.userId(), strings.getInvalidInputMessage())
            );
            return CompletableFuture.completedFuture(this);
        }

        return CompletableFuture.completedFuture(
            new EnterEntryDateState(new EnterEntryDateState.Data(input, Integer.parseInt(input)))
        );
    }

    record Data(String name) {
    }
}
