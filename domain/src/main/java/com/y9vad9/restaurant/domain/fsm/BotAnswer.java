package com.y9vad9.restaurant.domain.fsm;

import com.y9vad9.restaurant.domain.system.types.UserId;

import java.util.List;

public record BotAnswer(
    UserId userId,
    String message,
    List<List<String>> buttonsRow
) {
    public BotAnswer(UserId userId, String message) {
        this(userId, message, List.of());
    }
    public BotAnswer(UserId userId, String message, String buttonText) {
        this(userId, message, List.of(List.of(buttonText)));
    }
}
