plugins {
    application
    kotlin("jvm") version "1.9.22"
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
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

tasks {
    compileJava {
        // options.compilerArgs.add("--enable-preview")
        options.isIncremental = true
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "21"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "21"
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
