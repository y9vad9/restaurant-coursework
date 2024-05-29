package com.y9vad9.restaurant.domain.fsm.states;

import com.y9vad9.jfsm.FSMState;
import com.y9vad9.jfsm.context.ExecutorContextElement;
import com.y9vad9.jfsm.context.FSMContext;
import com.y9vad9.jfsm.functions.SendActionFunction;
import com.y9vad9.restaurant.domain.fsm.BotAnswer;
import com.y9vad9.restaurant.domain.fsm.BotState;
import com.y9vad9.restaurant.domain.fsm.IncomingMessage;
import com.y9vad9.restaurant.domain.fsm.states.admin.AdminMenuState;
import com.y9vad9.restaurant.domain.fsm.states.reservation.EnterYourNameState;
import com.y9vad9.restaurant.domain.system.repositories.SystemRepository;
import com.y9vad9.restaurant.domain.system.strings.Strings;
import com.y9vad9.restaurant.domain.tables.repository.TablesRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public record MainMenuState(Data data) implements BotState<MainMenuState.Data> {
    public static MainMenuState INSTANCE = new MainMenuState(
        new Data(Optional.empty())
    );

    record Data(Optional<Strings> localeOverride) {
    }

    @Override
    public CompletableFuture<FSMState<?, IncomingMessage, BotAnswer>> onEnter(
        IncomingMessage prevIntent,
        SendActionFunction<BotAnswer> sendAction,
        FSMContext context
    ) {
        Strings strings = data().localeOverride().orElseGet(() -> context.getElement(Strings.KEY));
        SystemRepository systemRepository = context.getElement(SystemRepository.KEY);

        if (systemRepository.getAdmins().stream().anyMatch(id -> prevIntent.userId().equals(id)))
            return CompletableFuture.completedFuture(new AdminMenuState(new AdminMenuState.Data(Optional.of(strings))));

        sendAction.execute(new BotAnswer(prevIntent.userId(), strings.getMainMenuMessage(), menuButtons(strings)));
        return BotState.super.onEnter(prevIntent, sendAction, context);
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
        final var strings = data().localeOverride().orElseGet(() -> context.getElement(Strings.KEY));

        return CompletableFuture.supplyAsync(
            () -> handleCommand(
                message,
                strings,
                sendAction,
                systemRepository,
                tablesRepository,
                context
            ),
            executor
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
            return EnterYourNameState.INSTANCE;
        } else if (command.equals(strings.getBookedTablesTitle())) {
            try {
                messageText = strings.getBookedTablesMessage(tablesRepository.getReservedTables(message.userId()).get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        } else if (command.equals(strings.getContactsTitle())) {
            messageText = strings.getContactsMessage(systemRepository.getContacts());
        } else if (command.equals(strings.getWhenWeWorkTitle())) {
            messageText = strings.getWhenWeWorkMessage(systemRepository.getSchedule());
        } else if(command.equals(strings.getChangeLanguageTitle())) {
            return ChooseLanguageState.INSTANCE;
        } else {
            messageText = strings.getUnknownCommandMessage();
        }

        if (messageText.isEmpty()) {
            onEnter(message, sendAction, context);
        }

        sendAction.execute(new BotAnswer(message.userId(), messageText));

        return this;
    }

    private static List<List<String>> menuButtons(Strings strings) {
        return List.of(
            List.of(strings.getBookTableTitle(), strings.getBookedTablesTitle()),
            List.of(strings.getContactsTitle(), strings.getWhenWeWorkTitle()),
            List.of(strings.getChangeLanguageTitle())
        );
    }
}
