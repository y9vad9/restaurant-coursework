package com.y9vad9.restaurant.telegram.bot;

import com.y9vad9.jfsm.FSMManager;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

public class TelegramBot implements LongPollingSingleThreadUpdateConsumer {
    private FSMManager<Long, Update, BotApiMethod<?>> fsmManager;
    private TelegramClient telegramClient;

    public static void startBot(String token) {

    }

    @Override
    public void consume(List<Update> updates) {
        LongPollingSingleThreadUpdateConsumer.super.consume(updates);
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            fsmManager.getStateMachine(update.getMessage().getChatId())
                .whenComplete(
                    (fsm, throwable) -> fsm.sendIntent(update, method -> {
                        try {
                            telegramClient.executeAsync(method);
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                    })
                );
        }
    }
}
