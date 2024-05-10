package com.y9vad9.restaurant.data.system.repository;

import com.y9vad9.restaurant.domain.system.repositories.SystemRepository;
import com.y9vad9.restaurant.domain.system.types.Contacts;
import com.y9vad9.restaurant.domain.system.types.Schedule;

import java.time.ZoneId;

public class ProvidedSystemRepository implements SystemRepository {
    private final Contacts contacts;
    private final Schedule schedule;
    private final ZoneId zoneId;

    public ProvidedSystemRepository(Contacts contacts, Schedule schedule, ZoneId zoneId) {
        this.contacts = contacts;
        this.schedule = schedule;
        this.zoneId = zoneId;
    }


    @Override
    public Schedule getSchedule() {
        return schedule;
    }

    @Override
    public Contacts getContacts() {
        return contacts;
    }

    @Override
    public ZoneId getZoneId() {
        return zoneId;
    }
}
