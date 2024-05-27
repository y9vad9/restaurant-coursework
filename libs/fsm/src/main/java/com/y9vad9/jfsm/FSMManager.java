package com.y9vad9.jfsm;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.y9vad9.jfsm.storage.StateStorage;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public final class FSMManager<TKey, TIntent, TAction> {
    private final StateStorage<TKey> storage;
    private final Cache<TKey, StateMachine<TKey, TIntent, TAction>> cache;

    public FSMManager(
        StateStorage<TKey> storage,
        Duration fsmAliveTime,
        int maxFsmInUse,
        Executor executor
    ) {
        this.storage = storage;
        this.cache = Caffeine.newBuilder()
            .expireAfterAccess(fsmAliveTime)
            .maximumSize(maxFsmInUse)
            .executor(executor)
            .evictionListener((key, value, cause) ->
                ((StateMachine<?, ?, ?>) Objects.requireNonNull(value)).close()
            )
            .build();
    }

    @SuppressWarnings("unchecked")
    public CompletableFuture<StateMachine<TKey, TIntent, TAction>> getStateMachine(
        TKey key
    ) {
        StateMachine<TKey, TIntent, TAction> saved = cache.getIfPresent(key);

        if (saved != null)
            return CompletableFuture.completedFuture(saved);

        return storage.loadState(key)
            .thenApply(state -> {
                StateMachine<TKey, TIntent, TAction> stateMachine = new StateMachine<>((FSMState<?, TIntent, TAction>) state, storage, key);
                cache.put(key, stateMachine);
                return stateMachine;
            });
    }
}
