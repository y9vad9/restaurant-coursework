package com.y9vad9.restaurant.domain.fsm.states;

import com.y9vad9.jfsm.FSMState;
import com.y9vad9.jfsm.context.FSMContext;
import com.y9vad9.jfsm.functions.SendActionFunction;
import com.y9vad9.restaurant.domain.fsm.BotAnswer;
import com.y9vad9.restaurant.domain.fsm.BotState;
import com.y9vad9.restaurant.domain.fsm.IncomingMessage;
import com.y9vad9.restaurant.domain.system.strings.EnglishStrings;
import com.y9vad9.restaurant.domain.system.strings.Strings;
import com.y9vad9.restaurant.domain.system.strings.UkrainianStrings;
import com.y9vad9.restaurant.domain.users.consumer.UsersLocaleConsumer;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public record ChooseLanguageState(Void data) implements BotState<Void> {
    public static ChooseLanguageState INSTANCE = new ChooseLanguageState(null);

    private static final String UKRAINIAN_LANG_OPTION = UkrainianStrings.INSTANCE.getLanguageDisplayTitle();
    private static final String ENGLISH_LANG_OPTION = EnglishStrings.INSTANCE.getLanguageDisplayTitle();

    @Override
    public CompletableFuture<FSMState<?, IncomingMessage, BotAnswer>> onEnter(
        IncomingMessage prevIntent,
        SendActionFunction<BotAnswer> sendAction,
        FSMContext context
    ) {
        String message = UkrainianStrings.INSTANCE.getChooseLanguageMessage() +
            "\n\n" + EnglishStrings.INSTANCE.getChooseLanguageMessage();

        sendAction.execute(
            new BotAnswer(
                prevIntent.userId(),
                message,
                List.of(List.of(UKRAINIAN_LANG_OPTION, ENGLISH_LANG_OPTION))
            )
        );
        return BotState.super.onEnter(prevIntent, sendAction, context);
    }

    @Override
    public CompletableFuture<FSMState<?, IncomingMessage, BotAnswer>> onIntent(
        IncomingMessage message,
        SendActionFunction<BotAnswer> sendAction,
        FSMContext context
    ) {
        final var languageConsumer = context.getElement(UsersLocaleConsumer.KEY);
        Strings strings;

        if (message.message().equals(UKRAINIAN_LANG_OPTION))
            strings = UkrainianStrings.INSTANCE;
        else if (message.message().equals(ENGLISH_LANG_OPTION))
            strings = EnglishStrings.INSTANCE;
        else {
            String failureMessage = UkrainianStrings.INSTANCE.getInvalidInputMessage() +
                "\n\n" + EnglishStrings.INSTANCE.getInvalidInputMessage();

            sendAction.execute(new BotAnswer(message.userId(), failureMessage));
            onEnter(message, sendAction, context);
            return CompletableFuture.completedFuture(this);
        }

        languageConsumer.consume(message.userId(), strings);

        return CompletableFuture.completedFuture(new MainMenuState(new MainMenuState.Data(Optional.of(strings))));
    }
}
