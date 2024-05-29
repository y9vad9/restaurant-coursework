plugins {
    java
    alias(libs.plugins.jooq)
}

dependencies {
    implementation(projects.libs.fsm)
    implementation(libs.rubenlagus.telegramBotsApi)
    implementation(libs.jackson.core)

    implementation(projects.domain)

    implementation(libs.jooq.core)
    jooqCodegen(libs.jooq.meta)

    implementation(libs.persistance.eclipselink)

    implementation(libs.jackson.core)
}

val jooqOutputDir = "build/generated/jooq"

java {
    sourceSets {
        named("main") {
            java.srcDirs(jooqOutputDir)
        }
    }
}

jooq {
    configuration {
        generator {
            database {
                name = "org.jooq.meta.extensions.ddl.DDLDatabase"
                properties {
                    property {
                        key = "scripts"
                        value = "src/main/jooq/database.sql"
                    }
                }
            }

            generate {
                isPojos = true
                isJavaTimeTypes = true
                isSequences = true
            }

            target {
                packageName = "com.y9vad9.restaurant.db.generated"
                directory = jooqOutputDir
            }
        }
    }
}