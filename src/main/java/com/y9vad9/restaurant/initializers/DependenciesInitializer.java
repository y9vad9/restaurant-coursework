package com.y9vad9.restaurant.initializers;

import com.y9vad9.jfsm.storage.CompoundStateStorage;
import com.y9vad9.jfsm.storage.StateStorage;
import com.y9vad9.restaurant.cli.Arguments;
import com.y9vad9.restaurant.data.fsm.DbBotStateStorage;
import com.y9vad9.restaurant.data.system.repository.ProvidedSystemRepository;
import com.y9vad9.restaurant.data.tables.repository.DbTablesRepository;
import com.y9vad9.restaurant.data.users.consumer.DbUsersLocaleConsumer;
import com.y9vad9.restaurant.data.users.provider.DbUsersLocaleProvider;
import com.y9vad9.restaurant.domain.fsm.states.InitialState;
import com.y9vad9.restaurant.domain.system.repositories.SystemRepository;
import com.y9vad9.restaurant.domain.system.types.UserId;
import com.y9vad9.restaurant.entities.Config;
import com.y9vad9.restaurant.entities.Dependencies;
import org.jooq.DSLContext;

import java.sql.SQLException;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.y9vad9.restaurant.initializers.DatabaseInitializer.initializeDatabase;

public final class DependenciesInitializer {

    public static Dependencies initializeDependencies(Config config, Arguments arguments) throws SQLException {
        SystemRepository systemRepository = initializeSystemRepository(config, arguments);
        DSLContext dslContext = initializeDatabase(arguments, config);
        ThreadPoolExecutor executor = createThreadPoolExecutor();
        StateStorage<UserId> inMemoryStatesStorage = StateStorage.inMemory(userId -> null);
        StateStorage<UserId> dbStatesStorage = new DbBotStateStorage(dslContext, executor, config.mapper(), userId -> null);
        List<StateStorage<UserId>> storages = List.of(inMemoryStatesStorage, dbStatesStorage);

        return new Dependencies(
            arguments,
            arguments.getNamed("token").orElseGet(() -> System.getenv("BOT_TOKEN")),
            systemRepository,
            new DbTablesRepository(dslContext, executor),
            new DbUsersLocaleProvider(dslContext, executor),
            new DbUsersLocaleConsumer(dslContext, executor),
            new CompoundStateStorage(storages, userId -> InitialState.INSTANCE)
        );
    }

    private static SystemRepository initializeSystemRepository(Config config, Arguments arguments) {
        ZoneId zoneId = ZoneId.of(arguments.getNamed("zoneId").orElse("Europe/Kyiv"));
        return new ProvidedSystemRepository(config.contacts(), config.schedule(), zoneId, config.adminIds());
    }

    private static ThreadPoolExecutor createThreadPoolExecutor() {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        int corePoolSize = availableProcessors * 2;
        int maximumPoolSize = availableProcessors * 2;
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new ThreadPoolExecutor.CallerRunsPolicy());
    }
}

