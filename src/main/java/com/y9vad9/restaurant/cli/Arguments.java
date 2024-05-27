package com.y9vad9.restaurant.cli;

import java.util.List;
import java.util.Optional;

/**
 * Клас, що надає зручний спосіб обробки аргументів командного рядка.
 */
public final class Arguments {

    private final List<String> raw;

    private Arguments(String[] arguments) {
        raw = List.of(arguments);
    }

    /**
     * Створює новий об'єкт {@code Arguments} з заданими аргументами командного рядка.
     *
     * @param arguments масив аргументів командного рядка
     * @return новий об'єкт {@code Arguments}
     */
    public static Arguments from(String[] arguments) {
        return new Arguments(arguments);
    }

    /**
     * Отримує значення, що відповідає заданому тегу.
     *
     * @param tag тег, який ідентифікує шукане значення
     * @return об'єкт {@link Optional}, який містить значення, якщо такий тег був знайдений
     */
    public Optional<String> getNamed(String tag) {
        return raw.stream()
            .filter(element -> element.startsWith("-" + tag))
            .findFirst()
            .flatMap(part -> part.replaceFirst("-" + tag + "=", "").describeConstable());
    }

    /**
     * Перевіряє, чи присутній заданий тег у списку аргументів.
     *
     * @param tag тег, який необхідно перевірити на присутність
     * @return {@code true}, якщо тег присутній серед аргументів, інакше - {@code false}
     */
    public boolean isPresent(String tag) {
        return raw.stream()
            .anyMatch(element -> element.equals("-" + tag));
    }
}
