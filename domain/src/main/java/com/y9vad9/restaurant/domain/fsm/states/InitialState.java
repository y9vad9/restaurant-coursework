package com.y9vad9.restaurant.domain.fsm.states;

import com.y9vad9.jfsm.FSMState;
import com.y9vad9.jfsm.context.FSMContext;
import com.y9vad9.jfsm.functions.SendActionFunction;
import com.y9vad9.restaurant.domain.fsm.BotAnswer;
import com.y9vad9.restaurant.domain.fsm.BotState;
import com.y9vad9.restaurant.domain.fsm.IncomingMessage;

import java.util.concurrent.CompletableFuture;

public class InitialState implements BotState<Void> {
    public static InitialState INSTANCE = new InitialState();

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
        return CompletableFuture.completedFuture(MainMenuState.INSTANCE);
    }
}
