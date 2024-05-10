package com.y9vad9.jfsm.storage;

import com.y9vad9.jfsm.FSMState;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public final class InMemoryStateStorage<TKey> implements StateStorage<TKey> {
    private final ConcurrentHashMap<TKey, FSMState<?, ?, ?>> statesMap = new ConcurrentHashMap<>();
    private final Function<TKey, FSMState<?, ?, ?>> defaultSupplier;

    public InMemoryStateStorage(Function<TKey, FSMState<?, ?, ?>> defaultSupplier) {
        this.defaultSupplier = defaultSupplier;
    }

    @Override
    public CompletableFuture<FSMState<?, ?, ?>> loadState(TKey key) {
        return CompletableFuture.completedFuture(
            statesMap.getOrDefault(key, defaultSupplier.apply(key))
        );
    }

    @Override
    public CompletableFuture<Void> saveState(TKey key, FSMState<?, ?, ?> state) {
        statesMap.put(key, state);
        return CompletableFuture.completedFuture(null);
    }
}
