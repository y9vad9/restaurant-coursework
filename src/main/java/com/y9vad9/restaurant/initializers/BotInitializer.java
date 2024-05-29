package com.y9vad9.restaurant.initializers;

import com.y9vad9.restaurant.entities.Dependencies;
import com.y9vad9.restaurant.telegram.bot.TelegramBotConsumer;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

import java.util.concurrent.TimeUnit;

public class BotInitializer {
    private static final int MAX_RETRIES = 5;
    private static final long INITIAL_TIMEOUT = 1000;
    private static final long MAX_TIMEOUT = TimeUnit.MINUTES.toMillis(5);

    public static void startBot(Dependencies dependencies) {
        int retryCount = 0;
        long timeout = INITIAL_TIMEOUT;

        while (retryCount < MAX_RETRIES) {
            try {
                TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
                TelegramBotConsumer botConsumer = new TelegramBotConsumer(
                    dependencies.apiToken(),
                    dependencies.systemRepository(),
                    dependencies.tablesRepository(),
                    dependencies.localeProvider(),
                    dependencies.localeConsumer(),
                    dependencies.stateStorage()
                );
                botsApplication.registerBot(dependencies.apiToken(), botConsumer).start();
                System.out.println("Бот розпочав свою роботу успішно.");
                addShutdownHook(botsApplication);
                Thread.currentThread().join();
                break;
            } catch (Exception e) {
                System.err.println("Не вдалось запустити бота: " + e + "\nПерезапуск через " + timeout + " мілісекунд...");
                sleep(timeout);
                retryCount++;
                timeout *= 2;
                timeout = Math.min(timeout, MAX_TIMEOUT);
            }
        }

        System.err.println("Не вдалось запустити бота: перевищено кількість можливих перезапусків ("
            + MAX_RETRIES + "). Виходимо...");
    }

    private static void addShutdownHook(TelegramBotsLongPollingApplication botsApplication) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                botsApplication.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
