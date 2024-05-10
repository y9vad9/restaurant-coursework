package com.y9vad9.restaurant;

import com.y9vad9.restaurant.domain.system.types.Contacts;
import com.y9vad9.restaurant.domain.system.types.Schedule;

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
        "07:30 – 18:00"
    );
}
