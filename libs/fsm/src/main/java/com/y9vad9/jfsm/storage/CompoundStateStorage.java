package com.y9vad9.jfsm.storage;

import com.y9vad9.jfsm.FSMState;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class CompoundStateStorage<TKey> implements StateStorage<TKey> {
    private final List<StateStorage<TKey>> storages;
    private final Function<TKey, FSMState<?, ?, ?>> defaultSupplier;

    public CompoundStateStorage(
        List<StateStorage<TKey>> storages,
        Function<TKey, FSMState<?, ?, ?>> defaultSupplier
    ) {
        this.storages = storages;
        this.defaultSupplier = defaultSupplier;
    }

    @Override
    public CompletableFuture<FSMState<?, ?, ?>> loadState(TKey key) {
        return CompletableFuture.supplyAsync(
            () -> {
                for (final var storage : storages) {
                    final var future = storage.loadState(key);
                    future.join();

                    try {
                        final var value = future.get();

                        if (value != null) {
                            System.out.println(value);
                            return value;
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }

                return defaultSupplier.apply(key);
            }
        );
    }

    @Override
    public CompletableFuture<Void> saveState(TKey key, FSMState<?, ?, ?> state) {
        System.out.println(state);
        return CompletableFuture.supplyAsync(
            () -> {
                storages.forEach(
                    storage -> storage.saveState(key, state).join()
                );

                return null;
            }
        ).exceptionally(e -> {
            e.printStackTrace();
            return null;
        }).thenApply(o -> null);
    }
}
