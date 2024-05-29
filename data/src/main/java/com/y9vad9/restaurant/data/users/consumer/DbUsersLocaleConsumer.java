package com.y9vad9.restaurant.data.users.consumer;

import com.y9vad9.restaurant.domain.system.strings.Strings;
import com.y9vad9.restaurant.domain.system.types.UserId;
import com.y9vad9.restaurant.domain.users.consumer.UsersLocaleConsumer;
import org.jooq.DSLContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static com.y9vad9.restaurant.db.generated.tables.Locales.LOCALES;

public class DbUsersLocaleConsumer implements UsersLocaleConsumer {
    final DSLContext context;
    final ExecutorService executorService;

    public DbUsersLocaleConsumer(DSLContext context, ExecutorService executorService) {
        this.context = context;
        this.executorService = executorService;
    }

    @Override
    public Future<Integer> consume(UserId userId, Strings strings) {
        System.out.println(strings);
        String code = strings.getLocale().getLanguage();
        return executorService.submit(
            () -> context
                .insertInto(LOCALES, LOCALES.USER_ID, LOCALES.LANG_CODE)
                .values(userId.toString(), code)
                .onDuplicateKeyUpdate()
                .set(LOCALES.LANG_CODE, code)
                .execute()
        );
    }
}
