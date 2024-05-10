package com.y9vad9.restaurant.domain.fsm.states;

import com.y9vad9.jfsm.FSMState;
import com.y9vad9.jfsm.context.ExecutorContextElement;
import com.y9vad9.jfsm.context.FSMContext;
import com.y9vad9.jfsm.functions.SendActionFunction;
import com.y9vad9.restaurant.domain.fsm.BotAnswer;
import com.y9vad9.restaurant.domain.fsm.BotState;
import com.y9vad9.restaurant.domain.fsm.IncomingMessage;
import com.y9vad9.restaurant.domain.system.repositories.SystemRepository;
import com.y9vad9.restaurant.domain.system.strings.Strings;
import com.y9vad9.restaurant.domain.tables.repository.TablesRepository;
import com.y9vad9.restaurant.domain.users.provider.UsersLocaleProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MainMenuState implements BotState<Void> {
    public static MainMenuState INSTANCE = new MainMenuState();

    @Override
    public CompletableFuture<FSMState<?, IncomingMessage, BotAnswer>> onEnter(IncomingMessage prevIntent, SendActionFunction<BotAnswer> sendAction, FSMContext context) {
        Strings strings = context.getElement(Strings.KEY);
        sendAction.execute(new BotAnswer(prevIntent.userId(), strings.getHelloMessage(), menuButtons(strings)));
        return BotState.super.onEnter(prevIntent, sendAction, context);
    }

    @Override
    public Void data() {
        return null;
    }

    @Override
    public CompletableFuture<FSMState<?, IncomingMessage, BotAnswer>> onIntent(
        IncomingMessage message,
        SendActionFunction<BotAnswer> sendAction,
        FSMContext context
    ) {
        final var executor = context.getElement(ExecutorContextElement.KEY)
            .executor();
        final var systemRepository = context.getElement(SystemRepository.KEY);
        final var tablesRepository = context.getElement(TablesRepository.KEY);

        return context.getElement(UsersLocaleProvider.KEY)
            .provide(message.userId())
            .thenCompose(strings -> CompletableFuture.supplyAsync(() -> handleCommand(
                    message,
                    strings,
                    sendAction,
                    systemRepository,
                    tablesRepository,
                    context
                ), executor)
            );
    }

    private FSMState<?, IncomingMessage, BotAnswer> handleCommand(
        IncomingMessage message,
        Strings strings,
        SendActionFunction<BotAnswer> sendAction,
        SystemRepository systemRepository,
        TablesRepository tablesRepository,
        FSMContext context
    ) {
        String command = message.message();
        String messageText;

        if (command.equals(strings.getBookTableTitle())) {
            onEnter(message, sendAction, context);
            return this;
        } else if (command.equals(strings.getBookedTablesTitle())) {
            try {
                messageText = strings.getBookedTablesMessage(tablesRepository.getReservedTables().get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        } else if (command.equals(strings.getContactsTitle())) {
            messageText = strings.getContactsMessage(systemRepository.getContacts());
        } else if (command.equals(strings.getWhenWeWorkTitle())) {
            messageText = strings.getWhenWeWorkMessage(systemRepository.getSchedule());
        } else {
            messageText = strings.getUnknownCommandMessage();
        }

        if (!messageText.isEmpty()) {
            onEnter(message, sendAction, context);
        }

        return this;
    }

    private static List<List<String>> menuButtons(Strings strings) {
        return List.of(
            List.of(strings.getBookTableTitle(), strings.getBookedTablesTitle()),
            List.of(strings.getContactsTitle(), strings.getWhenWeWorkTitle())
        );
    }
}
