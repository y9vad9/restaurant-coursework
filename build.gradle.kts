plugins {
    java
}

dependencies {
    implementation(projects.bot)
    implementation(libs.jackson.core)
    implementation(libs.jackson.jdk8)
    implementation(projects.domain)
}

//allprojects {
//    tasks.withType<JavaCompile> {
//        sourceCompatibility = "22"
//        targetCompatibility = "22"
//    }
//}