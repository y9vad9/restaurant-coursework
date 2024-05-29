package com.y9vad9.restaurant;

import com.y9vad9.restaurant.domain.system.types.Contacts;
import com.y9vad9.restaurant.domain.system.types.Schedule;
import com.y9vad9.restaurant.domain.system.types.UserId;
import com.y9vad9.restaurant.entities.TableCapacity;

import java.util.List;

public class ApplicationDefaults {
    public static final Contacts CONTACTS = new Contacts(
        List.of("+123 456 7890"),
        List.of("contact@email.com"),
        "Південний термінал, вулиця Георгія Кірпи, Вокзальна площа, 3 м, Київ, Україна, 03035"
    );

    public static final Schedule SCHEDULE = new Schedule(
        "07:30 – 22:00",
        "07:30 – 22:00",
        "07:30 – 22:00",
        "07:30 – 22:00",
        "07:30 – 22:00",
        "09:30 – 18:00",
        "09:30 – 18:00"
    );

    public static final List<TableCapacity> TABLE_CAPACITY_LIST = List.of(
        new TableCapacity(1, 4),
        new TableCapacity(2, 3),
        new TableCapacity(3, 2)
    );

    public static final List<UserId> ADMIN_LIST = List.of(
        new UserId(UserId.Platform.TELEGRAM, "999999999")
    );
}
