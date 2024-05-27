package com.y9vad9.restaurant.entities;

import com.y9vad9.restaurant.cli.Arguments;
import com.y9vad9.restaurant.domain.system.repositories.SystemRepository;
import com.y9vad9.restaurant.domain.tables.repository.TablesRepository;
import com.y9vad9.restaurant.domain.users.consumer.UsersLocaleConsumer;
import com.y9vad9.restaurant.domain.users.provider.UsersLocaleProvider;

public record Dependencies(
    Arguments arguments,
    String apiToken,
    SystemRepository systemRepository,
    TablesRepository tablesRepository,
    UsersLocaleProvider localeProvider,
    UsersLocaleConsumer localeConsumer
) {
}
