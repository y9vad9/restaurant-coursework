package com.y9vad9.restaurant.telegram.bot;

import com.y9vad9.jfsm.FSMManager;
import com.y9vad9.jfsm.context.ExecutorContextElement;
import com.y9vad9.jfsm.context.FSMContext;
import com.y9vad9.jfsm.functions.SendActionFunction;
import com.y9vad9.jfsm.storage.StateStorage;
import com.y9vad9.restaurant.domain.fsm.BotAnswer;
import com.y9vad9.restaurant.domain.fsm.IncomingMessage;
import com.y9vad9.restaurant.domain.system.repositories.SystemRepository;
import com.y9vad9.restaurant.domain.system.types.UserId;
import com.y9vad9.restaurant.domain.tables.repository.TablesRepository;
import com.y9vad9.restaurant.domain.users.consumer.UsersLocaleConsumer;
import com.y9vad9.restaurant.domain.users.provider.UsersLocaleProvider;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TelegramBotConsumer implements LongPollingSingleThreadUpdateConsumer {
    private TelegramClient telegramClient;

    private final FSMManager<UserId, IncomingMessage, BotAnswer> fsmManager;
    private final FSMContext globalContext;
    private final UsersLocaleProvider localeProvider;


    public TelegramBotConsumer(
        String token,
        SystemRepository repository,
        TablesRepository tablesRepository,
        UsersLocaleProvider localeProvider,
        UsersLocaleConsumer localeConsumer,
        StateStorage<UserId> stateStorage
    ) {
        Executor executor = new ThreadPoolExecutor(8, 12, Long.MAX_VALUE, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>());

        this.fsmManager = new FSMManager<>(
            stateStorage,
            Duration.ofMinutes(10),
            500,
            executor
        );
        this.localeProvider = localeProvider;
        this.telegramClient = new OkHttpTelegramClient(token);

        this.globalContext = FSMContext.of(repository, tablesRepository, localeConsumer, new ExecutorContextElement(executor));
    }

    private final SendActionFunction<BotAnswer> sendActionFunction = (method) -> {
        try {
            telegramClient.execute(mapToBotApiMethod(method));
        } catch (TelegramApiException e) {
            System.err.println("Помилка: ");
            e.printStackTrace();
            System.err.println("Відповідь бота, яка викликала помилку: " + method);
        }
    };

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            final var intent = mapFromUpdate(update);

            fsmManager.getStateMachine(intent.userId())
                .thenAccept(
                    fsm -> localeProvider.provide(
                        new UserId(
                            UserId.Platform.TELEGRAM,
                            update.getMessage().getChatId().toString()
                        )
                    ).thenAccept(
                        strings -> fsm.sendIntent(mapFromUpdate(update), sendActionFunction, globalContext.assign(strings))
                    ).exceptionally(throwable -> {
                        throwable.printStackTrace();
                        return null;
                    })
                );
        }
    }

    private BotApiMethod<?> mapToBotApiMethod(BotAnswer answer) {
        ReplyKeyboard keyboard = ReplyKeyboardMarkup.builder()
            .keyboard(
                answer.buttonsRow()
                    .stream()
                    .map(list -> new KeyboardRow(list.stream().map(KeyboardButton::new).toList()))
                    .toList()
            )
            .oneTimeKeyboard(true)
            .build();

        return SendMessage.builder()
            .chatId(answer.userId().userId())
            .text(answer.message())
            .parseMode(ParseMode.HTML)
            .replyMarkup(keyboard)
            .build();
    }

    private IncomingMessage mapFromUpdate(Update update) {
        return new IncomingMessage(
            new UserId(UserId.Platform.TELEGRAM, update.getMessage().getChatId().toString()),
            update.getMessage().getText()
        );
    }
}
