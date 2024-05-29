package com.y9vad9.restaurant.domain.system.strings;

import com.y9vad9.jfsm.context.FSMContextElement;
import com.y9vad9.restaurant.domain.system.types.Contacts;
import com.y9vad9.restaurant.domain.system.types.Schedule;
import com.y9vad9.restaurant.domain.tables.types.Table;

import java.util.List;
import java.util.Locale;

public interface Strings extends FSMContextElement {
    Key<Strings> KEY = Key.create();

    Locale getLocale();

    String getLanguageDisplayTitle();

    String getUnknownCommandMessage();

    String getHelloMessage();

    String getInvalidInputMessage();

    @Override
    default Key<?> key() {
        return KEY;
    }

    String getBookTableTitle();

    String getBookedTablesTitle();

    String getWhenWeWorkTitle();

    String getContactsTitle();

    String getWhenWeWorkMessage(Schedule schedule);

    String getContactsMessage(Contacts contacts);

    String getBookedTablesMessage(List<Table> tables);

    String getWriteNameForBookingMessage();

    String getWriteNumberOfPeopleForTableMessage();

    String getUnableToProceedCountOfPeopleMessage(int max);

    String getWriteDateOfEntryMessage();

    String getWriteEnterTimeMessage();

    String getWriteLeaveTimeMessage();

    String getSuccessfulBookingMessage(Table table);

    String getNoTablesAvailableMessage();

    String getWeDontWorkAtGivenDay();

    String getCancelTitle();

    String getNoAvailableDays();

    String getReservationsListAdmin();

    String getAdminHelloMessage();

    String getTodayTitle();

    String getSelectDateMessage();

    String getReservationsListAdminMessage(List<Table> tables);

    String getReservationCanceledRegular();

    String getReservationCanceled(Table.Reservation reservation, String reason);

    String getChooseLanguageMessage();
}
