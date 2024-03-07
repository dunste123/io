plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "io"


dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("jda", "5.0.0-beta.20")

            library("jda", "net.dv8tion", "JDA").versionRef("jda")
            library("logger", "ch.qos.logback", "logback-classic").version("1.4.11")
        }
    }
}


