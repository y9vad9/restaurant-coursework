package com.y9vad9.restaurant.domain.fsm;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.y9vad9.jfsm.FSMState;
import com.y9vad9.restaurant.domain.fsm.states.ChooseLanguageState;
import com.y9vad9.restaurant.domain.fsm.states.InitialState;
import com.y9vad9.restaurant.domain.fsm.states.MainMenuState;
import com.y9vad9.restaurant.domain.fsm.states.admin.AdminMenuState;
import com.y9vad9.restaurant.domain.fsm.states.reservation.EnterEntryDateState;
import com.y9vad9.restaurant.domain.fsm.states.reservation.EnterEntryTimeState;
import com.y9vad9.restaurant.domain.fsm.states.reservation.EnterGuestsNumberState;
import com.y9vad9.restaurant.domain.fsm.states.reservation.EnterYourNameState;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = InitialState.class, name = "initial"),
    @JsonSubTypes.Type(value = MainMenuState.class, name = "main_menu"),
    @JsonSubTypes.Type(value = ChooseLanguageState.class, name = "choose_language"),
    @JsonSubTypes.Type(value = AdminMenuState.class, name = "admin_menu"),
    @JsonSubTypes.Type(value = MainMenuState.class, name = "admin_reservations_list"),
    @JsonSubTypes.Type(value = EnterEntryDateState.class, name = "enter_entry_date"),
    @JsonSubTypes.Type(value = EnterEntryTimeState.class, name = "enter_entry_time"),
    @JsonSubTypes.Type(value = EnterGuestsNumberState.class, name = "enter_guests_number"),
    @JsonSubTypes.Type(value = EnterYourNameState.class, name = "enter_your_name")
})
public interface BotState<TData> extends FSMState<TData, IncomingMessage, BotAnswer> {
}