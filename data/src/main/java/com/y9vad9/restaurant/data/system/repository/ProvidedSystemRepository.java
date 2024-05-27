package com.y9vad9.restaurant.data.system.repository;

import com.y9vad9.restaurant.domain.system.repositories.SystemRepository;
import com.y9vad9.restaurant.domain.system.types.Contacts;
import com.y9vad9.restaurant.domain.system.types.Schedule;
import com.y9vad9.restaurant.domain.system.types.UserId;

import java.time.ZoneId;
import java.util.List;

public class ProvidedSystemRepository implements SystemRepository {
    private final Contacts contacts;
    private final Schedule schedule;
    private final ZoneId zoneId;
    private final List<UserId> admins;

    public ProvidedSystemRepository(Contacts contacts, Schedule schedule, ZoneId zoneId, List<UserId> admins) {
        this.contacts = contacts;
        this.schedule = schedule;
        this.zoneId = zoneId;
        this.admins = admins;
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

    @Override
    public List<UserId> getAdmins() {
        return admins;
    }
}
