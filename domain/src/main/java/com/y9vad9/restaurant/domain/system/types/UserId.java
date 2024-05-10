package com.y9vad9.restaurant.domain.system.types;

public record UserId(Platform platform, String userId) {
    public enum Platform {
        TELEGRAM
    }

    public static UserId fromString(String string) {
        int dividerIndex = string.indexOf(":");

        if (dividerIndex < 0)
            throw new IllegalArgumentException("Invalid input.");

        return new UserId(
            UserId.Platform.valueOf(string.substring(0, dividerIndex)),
            string.substring(dividerIndex + 1)
        );
    }

    @Override
    public String toString() {
        return platform().toString() + ":" + userId;
    }
}
