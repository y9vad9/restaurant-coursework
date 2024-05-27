package com.y9vad9.jfsm.context;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

final class ImmutableFSMContext implements FSMContext {
    static FSMContext EMPTY = new ImmutableFSMContext(null);

    private final Map<FSMContextElement.Key<?>, FSMContextElement> elements;

    ImmutableFSMContext(Map<FSMContextElement.Key<?>, FSMContextElement> elements) {
        this.elements = Objects.requireNonNullElse(elements, Collections.emptyMap());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends FSMContextElement> @Nullable E getElementOrNull(
        @NotNull FSMContextElement.Key<E> key
    ) {
        return (E) elements.get(key);
    }

    @Override
    public <E extends FSMContextElement> FSMContext assign(@NotNull E element) {
        Map<FSMContextElement.Key<?>, FSMContextElement> map = new HashMap<>(this.elements);
        map.put(element.key(), element);
        return new ImmutableFSMContext(Collections.unmodifiableMap(map));
    }

    @Override
    public <E extends FSMContextElement> FSMContext assign(@NotNull List<E> elements) {
        Map<FSMContextElement.Key<?>, FSMContextElement> map = new HashMap<>(this.elements);
        elements.forEach(element -> map.put(element.key(), element));
        return new ImmutableFSMContext(Collections.unmodifiableMap(map));
    }

    @Override
    public String toString() {
        return "ImmutableFSMContext(" +
            String.join(",", elements.values().stream().map(FSMContextElement::toString).toList()) +
            ')';
    }
}
