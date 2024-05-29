package com.y9vad9.restaurant.domain.system.strings;

import com.y9vad9.restaurant.domain.system.types.Contacts;
import com.y9vad9.restaurant.domain.system.types.Range;
import com.y9vad9.restaurant.domain.system.types.Schedule;
import com.y9vad9.restaurant.domain.tables.types.Table;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    public String getLanguageDisplayTitle() {
        return "🇺🇦 Українська";
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
    public String getMainMenuMessage() {
        return "Головне меню:";
    }

    @Override
    public String getInvalidInputMessage() {
        return "Неправильно введені дані.";
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
            " • Понеділок: " + ((schedule.monday() == null || schedule.monday().isEmpty()) ? "Зачинено" : schedule.monday()) + "\n" +
            " • Вівторок: " + ((schedule.tuesday() == null || schedule.tuesday().isEmpty()) ? "Зачинено" : schedule.tuesday()) + "\n" +
            " • Середа: " + ((schedule.wednesday() == null || schedule.wednesday().isEmpty()) ? "Зачинено" : schedule.wednesday()) + "\n" +
            " • Четвер: " + ((schedule.thursday() == null || schedule.thursday().isEmpty()) ? "Зачинено" : schedule.thursday()) + "\n" +
            " • П'ятниця: " + ((schedule.friday() == null || schedule.friday().isEmpty()) ? "Зачинено" : schedule.friday()) + "\n" +
            " • Субота: " + ((schedule.saturday() == null || schedule.saturday().isEmpty()) ? "Зачинено" : schedule.saturday()) + "\n" +
            " • Неділя: " + ((schedule.sunday() == null || schedule.sunday().isEmpty()) ? "Зачинено" : schedule.sunday()) + "\n";
    }

    @Override
    public String getContactsMessage(Contacts contacts) {
        StringBuilder builder = new StringBuilder()
            .append("<b>Наші контактні дані:</b>")
            .append("\n");

        switch (contacts.phoneNumbers().size()) {
            case 0:
                break;
            case 1:
                builder.append("    • Номер телефону: ").append(contacts.phoneNumbers().getFirst()).append("\n");
                break;
            default:
                String numbers = String.join(", ", contacts.phoneNumbers());
                builder.append("    • Номери телефону: ").append(numbers).append("\n");
                break;
        }

        switch (contacts.emailAddresses().size()) {
            case 0:
                break;
            case 1:
                builder.append("    • Електронна адреса: ").append(contacts.emailAddresses().getFirst()).append("\n");
                break;
            default:
                String addresses = String.join(", ", contacts.emailAddresses());
                builder.append("    • Електронні адреси: ").append(addresses).append("\n");
                break;
        }

        builder.append("    • Адреса: ").append(contacts.address());
        return builder.toString();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public String getBookedTablesMessage(List<Table> tables) {
        if (tables.isEmpty())
            return "Ви ще не зарезервували ні одного столику.";

        return "Ваші зарезервовані столики:\n\n" +
            tables.stream()
                .map(table -> {
                    final var reservation = table.reservation().get();
                    final var reservationTime = reservation.reservationTime();

                    return "<b> • Столик №" + table.number() + "</b>" +
                        "\n     Номер бронювання: " + reservation.id() +
                        "\n     На ім'я: " + reservation.fullName() +
                        "\n     Гостей: " + table.seats() +
                        "\n     Час: " + reservationTime.first().toLocalTime().toString() + " – " + reservationTime.last().toLocalTime() +
                        "\n     Дата: " + reservationTime.first().toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                })
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
    public String getUnableToProceedCountOfPeopleMessage(int max) {
        return "Нажаль, ми не маємо підходящих столиків для такої кількості людей, " +
            "спробуйте зарезервувати декілька. Максимум: " + max + ".";
    }

    @Override
    public String getWriteDateOfEntryMessage() {
        return "Напишіть дату, на яку хочете взяти бронювання:";
    }

    @Override
    public String getWriteEnterTimeMessage() {
        return "Оберіть час на який хочете забронювати (або введіть бажаний проміж часу за прикладом год:хв – год:хв):";
    }

    @Override
    public String getWriteLeaveTimeMessage() {
        return "Введіть час, коли бажаєте закінчити (в 24-годинному форматі за прикладом: година:хвилина):";
    }

    @Override
    public String getSuccessfulBookingMessage(Table table) {
        Table.Reservation reservation = table.reservation().orElseThrow();
        Range<LocalDateTime> reservationTime = reservation.reservationTime();
        return "Успішно заброньовано! Ваше бронювання:\n" +
            "   • Номер бронювання: " + reservation.id() + ".\n" +
            "   • Номер столику: " + table.number() + ".\n" +
            "   • Ім'я: " + reservation.fullName() + ".\n" +
            "   • Час: " + reservationTime.first().toLocalTime().toString() + " – " + reservationTime.last().toLocalTime() + ".\n" +
            "   • Дата: " + reservationTime.first().toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + ".";
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
        return "🔙 Скасувати";
    }

    @Override
    public String getNoAvailableDays() {
        return "Нажаль немає вільних дат для бронювання ☹️";
    }

    @Override
    public String getReservationsListAdmin() {
        return "Показати бронювання гостей";
    }

    @Override
    public String getAdminHelloMessage() {
        return "Вітаємо в адміністраторскій панелі. Тут ви можете переглядати бронювання" +
            " гостей та саморучно бронювати, якщо бронювання відбувається через телефон.";
    }

    @Override
    public String getTodayTitle() {
        return "Сьогодні";
    }

    @Override
    public String getSelectDateMessage() {
        return "Оберіть дату:";
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public String getReservationsListAdminMessage(List<Table> tables) {
        if (tables.isEmpty())
            return "Не було заброньовано ні одного столику за обраний період.";

        return "Зарезервовані столики:\n\n" +
            tables.stream()
                .map(table -> {
                    final var reservation = table.reservation().get();
                    final var reservationTime = reservation.reservationTime();

                    return "<b> • Столик №" + table.number() + "</b>" +
                        "\n     Номер бронювання: " + reservation.id() +
                        "\n     На ім'я: " + reservation.fullName() +
                        "\n     Гостей: " + table.seats() +
                        "\n     Час: " + reservationTime.first().toLocalTime().toString() + " – " + reservationTime.last().toLocalTime() +
                        "\n     Дата: " + reservationTime.first().toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                })
                .collect(Collectors.joining("\n")) +
            "\n\nВідмінити бронювання можна за допомогою команди /cancel [ідентифікатор] [причина]. " +
            "Повідомлення про це буде відправлено гостю.";
    }

    @Override
    public String getReservationCanceledRegular() {
        return "Бронювання успішно скасовано!";
    }

    @Override
    public String getReservationCanceled(Table.Reservation reservation, String reason) {
        return "Нажаль, ваше бронювання було скасовано: " + reason + ".";
    }

    @Override
    public String getChooseLanguageMessage() {
        return "Оберіть будь-ласка мову:";
    }
}
