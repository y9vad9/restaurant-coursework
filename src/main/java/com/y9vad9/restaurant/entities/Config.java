package com.y9vad9.restaurant.entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.y9vad9.restaurant.domain.system.types.Contacts;
import com.y9vad9.restaurant.domain.system.types.Schedule;
import com.y9vad9.restaurant.domain.system.types.UserId;

import java.util.List;

public record Config(
    Schedule schedule,
    Contacts contacts,
    List<TableCapacity> tableCapacities,
    List<UserId> adminIds,
    ObjectMapper mapper
) {
}
