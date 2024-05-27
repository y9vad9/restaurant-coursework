package com.y9vad9.restaurant.domain.fsm.states.reservation;

import com.y9vad9.jfsm.FSMState;
import com.y9vad9.jfsm.context.FSMContext;
import com.y9vad9.jfsm.functions.SendActionFunction;
import com.y9vad9.restaurant.domain.fsm.BotAnswer;
import com.y9vad9.restaurant.domain.fsm.BotState;
import com.y9vad9.restaurant.domain.fsm.IncomingMessage;
import com.y9vad9.restaurant.domain.fsm.states.MainMenuState;
import com.y9vad9.restaurant.domain.system.strings.Strings;
import com.y9vad9.restaurant.domain.tables.repository.TablesRepository;

import java.util.concurrent.CompletableFuture;

public record EnterGuestsNumberState(Data data) implements BotState<EnterGuestsNumberState.Data> {

    @Override
    public CompletableFuture<FSMState<?, IncomingMessage, BotAnswer>> onEnter(IncomingMessage prevIntent, SendActionFunction<BotAnswer> sendAction, FSMContext context) {
        final var strings = context.getElement(Strings.KEY);

        sendAction.execute(
            new BotAnswer(
                prevIntent.userId(),
                strings.getWriteNumberOfPeopleForTableMessage(),
                strings.getCancelTitle()
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
        String input = message.message();
        Strings strings = context.getElement(Strings.KEY);
        TablesRepository tablesRepository = context.getElement(TablesRepository.KEY);

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
            onEnter(message, sendAction, context);
            return CompletableFuture.completedFuture(this);
        }

        return tablesRepository.getMaxTableCapacity()
            .thenApply(result -> {
                if (result < guestsNumber) {
                    sendAction.execute(new BotAnswer(message.userId(), strings.getUnableToProceedCountOfPeopleMessage(result)));
                    onEnter(message, sendAction, context);
                    return EnterGuestsNumberState.this;
                } else return new EnterEntryDateState(new EnterEntryDateState.Data(data().name(), guestsNumber));
            });
    }

    record Data(String name) {
    }
}
