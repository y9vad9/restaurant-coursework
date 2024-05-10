package com.y9vad9.restaurant.domain.users.provider;

import com.y9vad9.jfsm.context.FSMContextElement;
import com.y9vad9.restaurant.domain.system.strings.Strings;
import com.y9vad9.restaurant.domain.system.types.UserId;

import java.util.concurrent.CompletableFuture;

public interface UsersLocaleProvider extends FSMContextElement {
    FSMContextElement.Key<UsersLocaleProvider> KEY = Key.create();

    CompletableFuture<Strings> provide(UserId userId);

    @Override
    default Key<?> key() {
        return KEY;
    }
}
