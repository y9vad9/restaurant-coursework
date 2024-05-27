package com.y9vad9.restaurant.domain.system.repositories;

import com.y9vad9.jfsm.context.FSMContextElement;
import com.y9vad9.restaurant.domain.system.types.Contacts;
import com.y9vad9.restaurant.domain.system.types.Schedule;
import com.y9vad9.restaurant.domain.system.types.UserId;

import java.time.ZoneId;
import java.util.List;

public interface SystemRepository extends FSMContextElement {
    FSMContextElement.Key<SystemRepository> KEY = FSMContextElement.Key.create();

    Schedule getSchedule();

    Contacts getContacts();

    ZoneId getZoneId();

    List<UserId> getAdmins();

    @Override
    default Key<?> key() {
        return KEY;
    }
}
