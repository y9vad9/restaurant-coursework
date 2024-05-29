plugins {
    java
}

dependencies {
    implementation(projects.bot)
    implementation(projects.domain)
    implementation(projects.data)

    implementation(libs.jackson.core)
    implementation(libs.jackson.jdk8)
    implementation(libs.jackson.jsr310)


    implementation(libs.rubenlagus.telegramBotsApi)

    implementation(libs.jooq.core)
    implementation(libs.h2)

    implementation(projects.libs.fsm)
}
