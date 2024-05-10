package com.y9vad9.jfsm;

import com.y9vad9.jfsm.context.FSMContext;
import com.y9vad9.jfsm.functions.SendActionFunction;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public final class StateMachine<TIntent, TAction> {
    private final BehaviorSubject<FSMState<?, TIntent, TAction>> state = BehaviorSubject.create();
    private FSMContext context;

    public <TData> StateMachine(
        FSMState<TData, TIntent, TAction> initialState,
        FSMContext context
    ) {
        this.state.onNext(initialState);
        this.context = context;
    }

    public CompletableFuture<FSMState<?, TIntent, TAction>> sendIntent(
        TIntent intent,
        SendActionFunction<TAction> sendAction
    ) {
        return Objects.requireNonNull(state.getValue()).onIntent(intent, sendAction, context);
    }

    public void updateFSMContext(Function<FSMContext, FSMContext> transformer) {
        this.context = transformer.apply(context);
    }

    public Observable<FSMState<?, TIntent, TAction>> getCurrentState() {
        return state;
    }


    public void close() {
        state.onComplete();
    }
}
