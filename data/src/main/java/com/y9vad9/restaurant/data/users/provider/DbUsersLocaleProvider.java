package com.y9vad9.restaurant.data.users.provider;

import com.y9vad9.restaurant.db.generated.tables.Locales;
import com.y9vad9.restaurant.domain.system.strings.EnglishStrings;
import com.y9vad9.restaurant.domain.system.strings.Strings;
import com.y9vad9.restaurant.domain.system.strings.UkrainianStrings;
import com.y9vad9.restaurant.domain.system.types.UserId;
import com.y9vad9.restaurant.domain.users.provider.UsersLocaleProvider;
import org.jooq.DSLContext;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static com.y9vad9.restaurant.db.generated.tables.Locales.LOCALES;

public class DbUsersLocaleProvider implements UsersLocaleProvider {
    final DSLContext context;
    final ExecutorService executorService;

    public DbUsersLocaleProvider(DSLContext context, ExecutorService executorService) {
        this.context = context;
        this.executorService = executorService;
    }

    @Override
    public CompletableFuture<Strings> provide(UserId userId) {
        return CompletableFuture.supplyAsync(
            () -> context.select(LOCALES.asterisk())
                    .from(LOCALES)
                    .where(LOCALES.USER_ID.eq(userId.toString()))
                    .fetchOne(LOCALES.LANG_CODE),
            executorService
        ).thenApply(code -> {
            if (code == null || UkrainianStrings.INSTANCE.getLocale().getLanguage().equals(code)) {
                return UkrainianStrings.INSTANCE;
            } else {
                return EnglishStrings.INSTANCE;
            }
        });
    }
}
