package com.y9vad9.restaurant.domain.system.types;

public record Range<T extends Comparable<? super T>>(T first, T last) {
    public static Range<?> EMPTY = new Range<>(null, null);

    public boolean within(T value) {
        return first.compareTo(value) >= 0 && value.compareTo(last) <= 0;
    }

    public boolean within(Range<T> other) {
        return other.first.compareTo(this.first) <= 0 && this.last.compareTo(other.last) <= 0;
    }
}
