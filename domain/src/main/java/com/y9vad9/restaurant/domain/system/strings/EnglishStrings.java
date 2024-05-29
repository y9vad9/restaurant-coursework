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

public final class EnglishStrings implements Strings {
    public static EnglishStrings INSTANCE = new EnglishStrings();

    @Override
    public Locale getLocale() {
        return Locale.of("en", "US");
    }

    @Override
    public String getLanguageDisplayTitle() {
        return "🏴󠁧󠁢󠁥󠁮󠁧󠁿 English";
    }

    @Override
    public String getUnknownCommandMessage() {
        return "Unknown command.";
    }

    @Override
    public String getHelloMessage() {
        return "Welcome to our restaurant's Telegram bot! You can book a table here for your desired time, " +
                "view your bookings, and check our working hours.";
    }

    @Override
    public String getInvalidInputMessage() {
        return "Invalid input.";
    }

    @Override
    public String getBookTableTitle() {
        return "📝 Book a Table";
    }

    @Override
    public String getBookedTablesTitle() {
        return "✅ View Booked Tables";
    }

    @Override
    public String getWhenWeWorkTitle() {
        return "🕑 When We Work?";
    }

    @Override
    public String getContactsTitle() {
        return "☎️ Contact Information";
    }

    @Override
    public String getWhenWeWorkMessage(Schedule schedule) {
        return "We operate according to the following schedule:\n" +
                " • Monday: " + ((schedule.monday() == null || schedule.monday().isEmpty()) ? "Closed" : schedule.monday()) + "\n" +
                " • Tuesday: " + ((schedule.tuesday() == null || schedule.tuesday().isEmpty()) ? "Closed" : schedule.tuesday()) + "\n" +
                " • Wednesday: " + ((schedule.wednesday() == null || schedule.wednesday().isEmpty()) ? "Closed" : schedule.wednesday()) + "\n" +
                " • Thursday: " + ((schedule.thursday() == null || schedule.thursday().isEmpty()) ? "Closed" : schedule.thursday()) + "\n" +
                " • Friday: " + ((schedule.friday() == null || schedule.friday().isEmpty()) ? "Closed" : schedule.friday()) + "\n" +
                " • Saturday: " + ((schedule.saturday() == null || schedule.saturday().isEmpty()) ? "Closed" : schedule.saturday()) + "\n" +
                " • Sunday: " + ((schedule.sunday() == null || schedule.sunday().isEmpty()) ? "Closed" : schedule.sunday()) + "\n";
    }

    @Override
    public String getContactsMessage(Contacts contacts) {
        StringBuilder builder = new StringBuilder()
                .append("Our contact details:")
                .append("\n");

        switch (contacts.phoneNumbers().size()) {
            case 0:
                break;
            case 1:
                builder.append("Phone number: ").append(contacts.phoneNumbers().getFirst()).append("\n");
                break;
            default:
                String numbers = String.join(", ", contacts.phoneNumbers());
                builder.append("Phone numbers: ").append(numbers).append("\n");
                break;
        }

        switch (contacts.emailAddresses().size()) {
            case 0:
                break;
            case 1:
                builder.append("Email address: ").append(contacts.emailAddresses().getFirst()).append("\n");
                break;
            default:
                String addresses = String.join(", ", contacts.emailAddresses());
                builder.append("Email addresses: ").append(addresses).append("\n");
                break;
        }

        builder.append("Address: ").append(contacts.address());
        return builder.toString();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public String getBookedTablesMessage(List<Table> tables) {
        if (tables.isEmpty())
            return "You haven't booked any tables yet.";

        return "Your booked tables:\n\n" +
                tables.stream()
                        .map(table -> {
                            final var reservation = table.reservation().get();
                            final var reservationTime = reservation.reservationTime();

                            return "<b> • Table No." + table.number() + "</b>" +
                                    "\n     Booking ID: " + reservation.id() +
                                    "\n     Name: " + reservation.fullName() +
                                    "\n     Guests: " + table.seats() +
                                    "\n     Time: " + reservationTime.first().toLocalTime().toString() + " – " + reservationTime.last().toLocalTime() +
                                    "\n     Date: " + reservationTime.first().toLocalDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                        })
                        .collect(Collectors.joining("\n"));
    }

    @Override
    public String getWriteNameForBookingMessage() {
        return "Enter the surname and name of the person for whom the booking will be made:";
    }

    @Override
    public String getWriteNumberOfPeopleForTableMessage() {
        return "Enter the number of people required for the table:";
    }

    @Override
    public String getUnableToProceedCountOfPeopleMessage(int max) {
        return "Unfortunately, we don't have suitable tables for this number of people, " +
            "try booking multiple. Maximum: " + max + ".";
    }

    @Override
    public String getWriteDateOfEntryMessage() {
        return "Write the date you want to book for:";
    }

    @Override
    public String getWriteEnterTimeMessage() {
        return "Select the time you want to book for (or enter the desired time range as example hour:minute - hour:minute):";
    }

    @Override
    public String getWriteLeaveTimeMessage() {
        return "Enter the time you want to leave (in 24-hour format as example: hour:minute):";
    }

    @Override
    public String getSuccessfulBookingMessage(Table table) {
        Table.Reservation reservation = table.reservation().orElseThrow();
        Range<LocalDateTime> reservationTime = reservation.reservationTime();
        return "Successfully booked! Your booking:\n" +
            "Booking ID: " + reservation.id() + ".\n" +
            "Table Number: " + table.number() + ".\n" +
            "Name: " + reservation.fullName() + ".\n" +
            "Time: " + reservationTime.first().toLocalTime().toString() + " – " + reservationTime.last().toLocalTime() + ".\n" +
            "Date: " + reservationTime.first().toLocalDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + ".";
    }

    @Override
    public String getNoTablesAvailableMessage() {
        return "No available tables";
    }

    @Override
    public String getWeDontWorkAtGivenDay() {
        return "Unfortunately, we don't work on this day ☹️";
    }

    @Override
    public String getCancelTitle() {
        return "🔙 Cancel";
    }

    @Override
    public String getNoAvailableDays() {
        return "Unfortunately, there are no available dates for booking ☹️";
    }

    @Override
    public String getReservationsListAdmin() {
        return "Show Guest Reservations";
    }

    @Override
    public String getAdminHelloMessage() {
        return "Welcome to the administrator panel. Here you can view guest reservations" +
            " and manually book if the booking is done via phone.";
    }

    @Override
    public String getTodayTitle() {
        return "Today";
    }

    @Override
    public String getSelectDateMessage() {
        return "Select the date:";
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public String getReservationsListAdminMessage(List<Table> tables) {
        if (tables.isEmpty())
            return "No tables were booked for the selected period.";

        return "Booked Tables:\n\n" +
            tables.stream()
                .map(table -> {
                    final var reservation = table.reservation().get();
                    final var reservationTime = reservation.reservationTime();

                    return "<b> • Table No." + table.number() + "</b>" +
                        "\n     Booking ID: " + reservation.id() +
                        "\n     Name: " + reservation.fullName() +
                        "\n     Guests: " + table.seats() +
                        "\n     Time: " + reservationTime.first().toLocalTime().toString() + " – " + reservationTime.last().toLocalTime() +
                        "\n     Date: " + reservationTime.first().toLocalDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                })
                .collect(Collectors.joining("\n")) +
            "\n\nYou can cancel the booking by using the command /cancel [identifier] [reason]. " +
            "A message will be sent to the guest about this.";
    }

    @Override
    public String getReservationCanceledRegular() {
        return "Booking canceled successfully!";
    }

    @Override
    public String getReservationCanceled(Table.Reservation reservation, String reason) {
        return "Unfortunately, your booking has been canceled: " + reason + ".";
    }

    @Override
    public String getChooseLanguageMessage() {
        return "Please select a language:";
    }
}

