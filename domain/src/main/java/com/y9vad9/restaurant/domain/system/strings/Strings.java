package com.y9vad9.restaurant.domain.system.strings;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.y9vad9.jfsm.context.FSMContextElement;
import com.y9vad9.restaurant.domain.system.types.Contacts;
import com.y9vad9.restaurant.domain.system.types.Schedule;
import com.y9vad9.restaurant.domain.tables.types.Table;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

@JsonSerialize(using = StringsSerializer.class)
@JsonDeserialize(using = StringsDeserializer.class)
public sealed interface Strings extends FSMContextElement permits EnglishStrings, UkrainianStrings {
    Key<Strings> KEY = Key.create();

    Locale getLocale();

    String getLanguageDisplayTitle();

    String getUnknownCommandMessage();

    String getHelloMessage();

    String getMainMenuMessage();

    String getInvalidInputMessage();

    String getChangeLanguageTitle();

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

class StringsSerializer extends StdSerializer<Strings> {

    protected StringsSerializer() {
        super(Strings.class);
    }

    @Override
    public void serialize(
        Strings strings,
        JsonGenerator jsonGenerator,
        SerializerProvider serializerProvider
    ) throws IOException {
        if (strings instanceof UkrainianStrings)
            jsonGenerator.writeString(UkrainianStrings.INSTANCE.getLocale().getLanguage());
        else jsonGenerator.writeString(EnglishStrings.INSTANCE.getLocale().getLanguage());
    }
}

class StringsDeserializer extends StdDeserializer<Strings> {
    protected StringsDeserializer() {
        super(Strings.class);
    }

    @Override
    public Strings deserialize(
        JsonParser jsonParser,
        DeserializationContext deserializationContext
    ) throws IOException {
        String type = jsonParser.getValueAsString();

        if (type.equals(UkrainianStrings.INSTANCE.getLocale().getLanguage()))
            return UkrainianStrings.INSTANCE;
        else return EnglishStrings.INSTANCE;
    }
}