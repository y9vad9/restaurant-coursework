package com.y9vad9.jfsm.context;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public sealed interface FSMContext permits ImmutableFSMContext {
    static FSMContext empty() {
        return ImmutableFSMContext.EMPTY;
    }

    static FSMContext of(FSMContextElement... elements) {
        return empty().assign(List.of(elements));
    }

    <E extends FSMContextElement> @Nullable E getElementOrNull(@NotNull FSMContextElement.Key<E> key);

    default <E extends FSMContextElement> E getElement(@NotNull FSMContextElement.Key<E> key) throws NullPointerException {
        return Objects.requireNonNull(getElementOrNull(key));
    }

    <E extends FSMContextElement> FSMContext assign(@NotNull E element);

    <E extends FSMContextElement> FSMContext assign(List<@NotNull E> elements);

    default FSMContext assign(FSMContextElement... elements) {
        return assign(List.of(elements));
    }
}
