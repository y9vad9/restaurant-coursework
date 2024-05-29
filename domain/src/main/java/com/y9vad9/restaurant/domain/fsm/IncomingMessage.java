package com.y9vad9.restaurant.domain.fsm;

import com.y9vad9.restaurant.domain.system.types.UserId;

public record IncomingMessage(
    UserId userId,
    String message
) {
}
