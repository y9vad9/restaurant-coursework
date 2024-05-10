package com.y9vad9.restaurant.domain.users.consumer;

import com.y9vad9.jfsm.context.FSMContextElement;
import com.y9vad9.restaurant.domain.system.strings.Strings;
import com.y9vad9.restaurant.domain.system.types.UserId;

import java.util.concurrent.CompletableFuture;

public interface UsersLocaleConsumer extends FSMContextElement {
    FSMContextElement.Key<UsersLocaleConsumer> KEY = Key.create();

    CompletableFuture<Void> consume(UserId userId, Strings strings);

    @Override
    default Key<?> key() {
        return KEY;
    }
}
