package com.y9vad9.jfsm.context;

import java.util.concurrent.Executor;

public record ExecutorContextElement(Executor executor) implements FSMContextElement {
    public static FSMContextElement.Key<ExecutorContextElement> KEY = FSMContextElement.Key.create();

    @Override
    public Key<?> key() {
        return KEY;
    }
}
