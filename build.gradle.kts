plugins {
    java
}

dependencies {
    implementation(projects.bot)
    implementation(projects.domain)
    implementation(projects.data)

    implementation(libs.jackson.core)
    implementation(libs.jackson.jdk8)

    implementation(libs.rubenlagus.telegramBotsApi)

    implementation("org.slf4j:slf4j-simple:2.0.13")

    implementation(libs.jooq.core)
    implementation(libs.h2)
}
