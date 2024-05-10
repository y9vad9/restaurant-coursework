package com.y9vad9.restaurant.domain.fsm;

import com.y9vad9.jfsm.FSMState;
import com.y9vad9.jfsm.context.FSMContext;
import com.y9vad9.jfsm.functions.SendActionFunction;

import java.util.concurrent.CompletableFuture;

public interface BotState<TData> extends FSMState<TData, IncomingMessage, BotAnswer> {}