package com.y9vad9.jfsm;

import com.y9vad9.jfsm.context.FSMContext;
import com.y9vad9.jfsm.functions.SendActionFunction;
import com.y9vad9.jfsm.storage.StateStorage;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public final class StateMachine<TKey, TIntent, TAction> {
    private final BehaviorSubject<FSMState<?, TIntent, TAction>> state = BehaviorSubject.create();
    private final StateStorage<TKey> storage;
    private final TKey key;

    public <TData> StateMachine(
        FSMState<TData, TIntent, TAction> initialState,
        StateStorage<TKey> storage,
        TKey key
    ) {
        this.state.onNext(initialState);
        this.storage = storage;
        this.key = key;
    }

    public CompletableFuture<? extends FSMState<?, TIntent, TAction>> sendIntent(
        TIntent intent,
        SendActionFunction<TAction> sendAction,
        FSMContext context
    ) {
        return Objects.requireNonNull(state.getValue()).onIntent(intent, sendAction, context)
            .exceptionally(throwable -> {
                throwable.printStackTrace();
                return null;
            })
            .thenApply(state -> {
                if (state != this.state.getValue()) {
                    this.state.onNext(state);
                    FSMState<?, TIntent, TAction> onEnterResult;

                    try {
                        onEnterResult = state.onEnter(intent, sendAction, context).get();

                        while (!onEnterResult.equals(this.state.getValue())) {
                            this.state.onNext(onEnterResult);
                            onEnterResult = this.state.getValue().onEnter(intent, sendAction, context).get();
                        }
                        storage.saveState(key, this.state.getValue());
                        return this.state.getValue();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }
                return state;
            }).whenCompleteAsync((state, exception) -> {
                exception.printStackTrace();
            });
    }

    public Observable<FSMState<?, TIntent, TAction>> getCurrentState() {
        return state;
    }


    public void close() {
        state.onComplete();
    }
}
