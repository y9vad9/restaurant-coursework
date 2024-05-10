package com.y9vad9.jfsm.storage;

import com.y9vad9.jfsm.FSMState;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface StateStorage<TKey> {
    static <TKey> StateStorage<TKey> inMemory(
        Function<TKey, FSMState<?, ?, ?>> defaultSupplier
    ) {
        return new InMemoryStateStorage<>(defaultSupplier);
    }

    CompletableFuture<FSMState<?, ?, ?>> loadState(TKey key);

    CompletableFuture<Void> saveState(TKey key, FSMState<?, ?, ?> state);
}
