plugins {
    java
}

dependencies {
    constraints {
        apiElements(libs.rxjava3)
    }

    implementation(libs.rxjava3)
    implementation(libs.caching.caffeine)

    implementation(libs.jetbrains.annotations)
}