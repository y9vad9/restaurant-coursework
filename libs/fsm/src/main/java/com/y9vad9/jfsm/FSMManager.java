package com.y9vad9.jfsm;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.y9vad9.jfsm.storage.StateStorage;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public final class FSMManager<TKey, TIntent, TAction> {
    private final StateStorage<TKey> storage;
    private final Cache<TKey, StateMachine<TIntent, TAction>> cache;
    private final Supplier<FSMState<?, TIntent, TAction>> initializer;

    public FSMManager(
        StateStorage<TKey> storage,
        Duration fsmAliveTime,
        int maxFsmInUse,
        Executor executor,
        Supplier<FSMState<?, TIntent, TAction>> initializer
        ) {
        this.storage = storage;
        this.cache = Caffeine.newBuilder()
            .expireAfterAccess(fsmAliveTime)
            .maximumSize(maxFsmInUse)
            .executor(executor)
            .evictionListener((key, value, cause) ->
                ((StateMachine<?, ?>) Objects.requireNonNull(value)).close()
            )
            .build();
        this.initializer = initializer;
    }

    @SuppressWarnings("unchecked")
    public CompletableFuture<StateMachine<TIntent, TAction>> getStateMachine(
        TKey key
    ) {
        StateMachine<TIntent, TAction> saved = cache.getIfPresent(key);

        if (saved != null)
            return CompletableFuture.completedFuture(saved);

        return storage.loadState(key)
            .thenApply(state -> {
                throw new IllegalArgumentException("");
//                StateMachine<TIntent, TAction> stateMachine = (FSMState<?, TIntent, TAction>) state;
//                cache.put(key, stateMachine);
//                return stateMachine;
            });
    }
}
