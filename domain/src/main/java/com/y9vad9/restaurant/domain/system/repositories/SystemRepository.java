package com.y9vad9.restaurant.domain.system.repositories;

import com.y9vad9.jfsm.context.FSMContextElement;
import com.y9vad9.restaurant.domain.system.types.Contacts;
import com.y9vad9.restaurant.domain.system.types.Schedule;

import java.time.ZoneId;

public interface SystemRepository extends FSMContextElement {
    FSMContextElement.Key<SystemRepository> KEY = FSMContextElement.Key.create();

    Schedule getSchedule();

    Contacts getContacts();

    ZoneId getZoneId();

    @Override
    default Key<?> key() {
        return KEY;
    }
}
