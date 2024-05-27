package com.y9vad9.restaurant;

import com.y9vad9.restaurant.cli.Arguments;
import com.y9vad9.restaurant.entities.Config;
import com.y9vad9.restaurant.entities.Dependencies;
import com.y9vad9.restaurant.initializers.BotInitializer;
import com.y9vad9.restaurant.initializers.ConfigInitializer;
import com.y9vad9.restaurant.initializers.DependenciesInitializer;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws IOException, SQLException {
        Arguments arguments = Arguments.from(args);
        Config config = ConfigInitializer.initializeConfig();
        Dependencies dependencies = DependenciesInitializer.initializeDependencies(config, arguments);

        BotInitializer.startBot(dependencies);
    }
}
