package com.y9vad9.jfsm.functions;

@FunctionalInterface
public interface SendActionFunction<TAction> {
    void execute(TAction action);
}
