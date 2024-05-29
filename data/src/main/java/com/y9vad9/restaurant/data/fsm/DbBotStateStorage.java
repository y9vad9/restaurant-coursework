package com.y9vad9.restaurant.data.fsm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.y9vad9.jfsm.FSMState;
import com.y9vad9.jfsm.storage.StateStorage;
import com.y9vad9.restaurant.domain.fsm.BotState;
import com.y9vad9.restaurant.domain.system.types.UserId;
import org.jooq.DSLContext;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

import static com.y9vad9.restaurant.db.generated.tables.States.STATES;

public class DbBotStateStorage implements StateStorage<UserId> {
    final DSLContext context;
    final ExecutorService executorService;
    final ObjectMapper objectMapper;
    final Function<UserId, BotState<Object>> fallback;

    public DbBotStateStorage(
        DSLContext context,
        ExecutorService executorService,
        ObjectMapper objectMapper,
        Function<UserId, BotState<Object>> fallback
    ) {
        this.context = context;
        this.executorService = executorService;
        this.objectMapper = objectMapper;
        this.fallback = fallback;
    }

    @Override
    public CompletableFuture<FSMState<?, ?, ?>> loadState(UserId userId) {
        return CompletableFuture.supplyAsync(
            () -> {
                String json = context.selectFrom(STATES)
                    .where(STATES.USER_ID.eq(userId.toString()))
                    .fetchOne(STATES.JSON_VALUE);

                if (json == null)
                    return fallback.apply(userId);

                try {
                    return objectMapper.readValue(json, BotState.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            },
            executorService
        );
    }

    @Override
    public CompletableFuture<Void> saveState(UserId userId, FSMState<?, ?, ?> state) {
        if (!(state instanceof BotState<?>))
            throw new IllegalArgumentException("Unable to process instances that does not inherit BotState.");

        try {
            String json = objectMapper.writeValueAsString(state);
            return CompletableFuture.supplyAsync(
                () -> {
                    context.insertInto(STATES, STATES.USER_ID, STATES.JSON_VALUE)
                        .values(userId.toString(), json)
                        .onDuplicateKeyUpdate()
                        .set(STATES.JSON_VALUE, json)
                        .execute();

                    return null;
                }, executorService
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
