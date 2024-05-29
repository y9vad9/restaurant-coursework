package com.y9vad9.restaurant.initializers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.y9vad9.restaurant.ApplicationDefaults;
import com.y9vad9.restaurant.domain.system.types.Contacts;
import com.y9vad9.restaurant.domain.system.types.Schedule;
import com.y9vad9.restaurant.domain.system.types.UserId;
import com.y9vad9.restaurant.entities.Config;
import com.y9vad9.restaurant.entities.TableCapacity;

import java.io.File;
import java.io.IOException;
import java.util.List;

public final class ConfigInitializer {
    public static Config initializeConfig() throws IOException {
        File workingDir = new File(System.getProperty("user.dir") + "/restaurant");

        if (!workingDir.exists()) {
            workingDir.mkdirs();
        }

        File contactsFile = new File(workingDir, "contacts.json");
        File scheduleFile = new File(workingDir, "schedule.json");
        File tablesCapacitiesFile = new File(workingDir, "tables.json");
        File adminListFile = new File(workingDir, "admins.json");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        if (!contactsFile.exists()) {
            contactsFile.createNewFile();
            objectMapper.writeValue(contactsFile, ApplicationDefaults.CONTACTS);
            System.out.println("Файл для конфігурації контактних даних створено в " + contactsFile.getAbsolutePath() + ".");
        }

        if (!scheduleFile.exists()) {
            scheduleFile.createNewFile();
            objectMapper.writeValue(scheduleFile, ApplicationDefaults.SCHEDULE);
            System.out.println("Файл для конфігурації розкладу створено в " + scheduleFile.getAbsolutePath() + ".");
        }

        if (!tablesCapacitiesFile.exists()) {
            tablesCapacitiesFile.createNewFile();
            objectMapper.writeValue(tablesCapacitiesFile, ApplicationDefaults.TABLE_CAPACITY_LIST);
            System.out.println("Файл для конфігурації столиків створено в " + tablesCapacitiesFile.getAbsolutePath() + ".");
        }

        if (!adminListFile.exists()) {
            adminListFile.createNewFile();
            objectMapper.writeValue(adminListFile, ApplicationDefaults.ADMIN_LIST);
            System.out.println("Файл для конфігурації столиків створено в " + adminListFile.getAbsolutePath() + ".");
        }

        Contacts contacts = objectMapper.readValue(contactsFile, Contacts.class);
        Schedule schedule = objectMapper.readValue(scheduleFile, Schedule.class);
        List<TableCapacity> capacities = objectMapper.readValue(tablesCapacitiesFile, new TypeReference<>() {});
        List<UserId> adminList = objectMapper.readValue(adminListFile, new TypeReference<>() {});

        return new Config(schedule, contacts, capacities, adminList, objectMapper);
    }
}
