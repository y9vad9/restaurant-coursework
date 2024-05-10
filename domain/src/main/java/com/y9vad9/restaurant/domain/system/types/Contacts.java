package com.y9vad9.restaurant.domain.system.types;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public record Contacts(
    @Nullable List<String> phoneNumbers,
    @Nullable List<String> emailAddresses,
    @Nullable String address
) {
}
