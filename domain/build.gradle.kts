plugins {
    java
}

dependencies {
    implementation(projects.libs.fsm)
    implementation(libs.jetbrains.annotations)

    implementation(libs.jackson.core)
    implementation(libs.jackson.databind)
    implementation(libs.jackson.core.annotations)
}