package com.y9vad9.restaurant.domain.system.types;

public record Range<T extends Comparable<? super T>>(T first, T last) {
    public boolean within(T value) {
        return first.compareTo(value) >= 0 && value.compareTo(last) <= 0;
    }
}
