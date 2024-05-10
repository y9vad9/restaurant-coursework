package com.y9vad9.restaurant;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.y9vad9.restaurant.domain.system.types.Contacts;
import com.y9vad9.restaurant.domain.system.types.Schedule;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Arguments arguments = Arguments.from(args);
        File workingDir = new File(System.getProperty("user.dir") + "/restaurant");
        File contactsFile = new File(workingDir, "contacts.json");
        File scheduleFile = new File(workingDir, "schedule.json");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        if (workingDir.mkdirs()) {
            System.out.println("Робоча директорія була створена в " + workingDir.getAbsolutePath());
        }

        Contacts contacts;
        Schedule schedule;

        if (!contactsFile.exists()) {
            contactsFile.createNewFile();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(contactsFile, ApplicationDefaults.CONTACTS);
            contacts = ApplicationDefaults.CONTACTS;
        } else {
            contacts = objectMapper.readValue(contactsFile, Contacts.class);
        }

        if (!scheduleFile.exists()) {
            contactsFile.createNewFile();
            objectMapper.writeValue(scheduleFile, ApplicationDefaults.SCHEDULE);
            schedule = ApplicationDefaults.SCHEDULE;
        } else {
            schedule = objectMapper.readValue(scheduleFile, Schedule.class);
        }
    }
}
