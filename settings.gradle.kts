plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "io"


dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("jda", "5.0.0-beta.20")
            version("jackson", "2.16.0")

            library("jda", "net.dv8tion", "JDA").versionRef("jda")
            library("logger", "ch.qos.logback", "logback-classic").version("1.4.11")

            library("jackson-core", "com.fasterxml.jackson.core", "jackson-core").versionRef("jackson")
            library("jackson-kotlin", "com.fasterxml.jackson.module", "jackson-module-kotlin").versionRef("jackson")
            library("jackson-yaml", "com.fasterxml.jackson.dataformat", "jackson-dataformat-yaml").versionRef("jackson")
        }
    }
}


