package com.y9vad9.jfsm.context;

public interface FSMContextElement {
    Key<?> key();

    @SuppressWarnings("unused")
    interface Key<E extends FSMContextElement> {
        static <E extends FSMContextElement> Key<E> create() {
            return new Key<>() {
            };
        }
    }
}
