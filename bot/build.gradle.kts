plugins {
    java
}

dependencies {
    implementation(projects.libs.fsm)
    implementation(libs.rubenlagus.telegramBotsApi)
    implementation(libs.rubenlagus.telegramClientApi)
    implementation(libs.jackson.core)

    implementation(projects.domain)
}