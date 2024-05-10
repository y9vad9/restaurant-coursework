package com.y9vad9.jfsm;

import com.y9vad9.jfsm.context.FSMContext;
import com.y9vad9.jfsm.functions.SendActionFunction;

import java.util.concurrent.CompletableFuture;

public interface FSMState<TData, TIntent, TAction> {
    TData data();

    CompletableFuture<FSMState<?, TIntent, TAction>> onIntent(
        TIntent intent,
        SendActionFunction<TAction> sendAction,
        FSMContext context
    );

    default CompletableFuture<FSMState<?, TIntent, TAction>> onEnter(
        TIntent prevIntent,
        SendActionFunction<TAction> sendAction,
        FSMContext context
    ) {
        return CompletableFuture.completedFuture(this);
    }
}
