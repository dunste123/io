import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    application
    kotlin("jvm") version "2.2.21"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "me.duncte123"
version = "0.0.1"

application {
    mainClass.set("${project.group}.io.MainKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.jda)
    implementation(libs.logger)
    implementation(libs.jackson.core)
    implementation(libs.jackson.kotlin)
    implementation(libs.jackson.yaml)
    implementation(kotlin("scripting-jsr223")) // Dangerous stuff!!
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // TODO: cleanup
    implementation(group = "com.github.vladimir-bukhtoyarov", name = "bucket4j-core", version = "4.10.0")
    implementation(group = "net.jodah", name = "expiringmap", version = "0.5.11")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

tasks {
    compileJava {
        // options.compilerArgs.add("--enable-preview")
        options.isIncremental = true
    }

    wrapper {
        gradleVersion = "8.5"
        distributionType = Wrapper.DistributionType.BIN
    }

    shadowJar {
        archiveClassifier.set("shadow")

        manifest {
            attributes["Description"] = "Super Hiro > Eduard"
        }
    }
}
