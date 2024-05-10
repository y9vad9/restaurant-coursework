package com.y9vad9.restaurant.domain.system.strings;

import com.y9vad9.restaurant.domain.system.types.Contacts;
import com.y9vad9.restaurant.domain.system.types.Schedule;
import com.y9vad9.restaurant.domain.tables.types.Table;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public final class UkrainianStrings implements Strings {
    public static UkrainianStrings INSTANCE = new UkrainianStrings();

    @Override
    public Locale getLocale() {
        return Locale.of("uk", "UA");
    }

    @Override
    public String getUnknownCommandMessage() {
        return "Невідома команда.";
    }

    @Override
    public String getHelloMessage() {
        return "Вітаємо в телеграм боті нашого ресторану! Ви можете забронювати " +
            "тут стіл на потрібний вам час, подивитись ваші бронювання та перевірити коли ми працюємо.";
    }

    @Override
    public String getInvalidInputMessage() {
        return "";
    }

    @Override
    public String getBookTableTitle() {
        return "📝 Забронювати столик";
    }

    @Override
    public String getBookedTablesTitle() {
        return "✅ Переглянути заброньовані столики";
    }

    @Override
    public String getWhenWeWorkTitle() {
        return "🕑 Коли ми працюємо?";
    }

    @Override
    public String getContactsTitle() {
        return "☎️ Контактна інформація";
    }

    @Override
    public String getWhenWeWorkMessage(Schedule schedule) {
        return "Ми працюємо за наступним графіком:\n" +
            " • Понеділок: " + schedule.monday() + "\n" +
            " • Вівторок: " + schedule.tuesday() + "\n" +
            " • Середа: " + schedule.wednesday() + "\n" +
            " • Четвер: " + schedule.thursday() + "\n" +
            " • П'ятниця: " + schedule.friday() + "\n" +
            " • Субота: " + schedule.saturday() + "\n" +
            " • Неділя: " + schedule.sunday() + "\n";
    }

    @Override
    public String getContactsMessage(Contacts contacts) {
        StringBuilder builder = new StringBuilder()
            .append("Наші контактні дані:")
            .append("\n");

        switch (contacts.phoneNumbers().size()) {
            case 0:
                break;
            case 1:
                builder.append("Номер телефону: ").append(contacts.phoneNumbers().getFirst()).append("\n");
                break;
            default:
                String numbers = String.join(", ", contacts.phoneNumbers());
                builder.append("Номери телефону: ").append(numbers).append("\n");
                break;
        }

        switch (contacts.emailAddresses().size()) {
            case 0:
                break;
            case 1:
                builder.append("Електронна адреса: ").append(contacts.emailAddresses().getFirst()).append("\n");
                break;
            default:
                String addresses = String.join(", ", contacts.emailAddresses());
                builder.append("Електронні адреси: ").append(addresses).append("\n");
                break;
        }

        builder.append("Адреса: ").append(contacts.address());
        return builder.toString();
    }

    @Override
    public String getBookedTablesMessage(List<Table> tables) {
        if (tables.isEmpty())
            return "Ви ще не зарезервували ні одного столику.";
        return "Ваші зарезервовані столики:\n" +
            tables.stream().map(table -> " • Столик номер " + table.number() + ", макс. гостей " + table.seats() + ".")
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String getWriteNameForBookingMessage() {
        return "Введіть прізвище та ім'я того, на кого буде бронювання:";
    }

    @Override
    public String getWriteNumberOfPeopleForTableMessage() {
        return "Введіть кількість людей, яка потрібна для столику:";
    }

    @Override
    public String getUnableToProceedCountOfPeopleMessage() {
        return "Нажаль, ми не маємо підходящих столиків для такої кількості людей, " +
            "спробуйте зарезервувати декілька.";
    }

    @Override
    public String getWriteDateOfEntryMessage() {
        return "";
    }

    @Override
    public String getWriteEnterTimeMessage() {
        return "Введіть час на який бажаєте забронювати (в 24-годинному форматі за прикладом: година:хвилина):";
    }

    @Override
    public String getWriteLeaveTimeMessage() {
        return "Введіть час, коли бажаєте закінчити (в 24-годинному форматі за прикладом: година:хвилина):";
    }

    @Override
    public String getSuccessfulBookingMessage(Table table) {
        // safe to use orElseThrow – we always have reservation in this case.
        Table.Reservation reservation = table.reservation().orElseThrow();
        return "Успішно заброньовано! Ваше бронювання:" +
            "Номер столику: " + table.number() + ".\n" +
            "Ім'я: " + reservation.fullName() + ".\n" +
            // TODO: time formatting
            "Час бронювання: " + reservation.reservationTime().first().toString() +
            "До: " + reservation.reservationTime().last().toString();
    }

    @Override
    public String getNoTablesAvailableMessage() {
        return "Немає доступних столиків";
    }

    @Override
    public String getWeDontWorkAtGivenDay() {
        return "Нажаль, в цей день ми не працюємо ☹️";
    }

    @Override
    public String getCancelTitle() {
        return "🔙 Відмінити";
    }
}
