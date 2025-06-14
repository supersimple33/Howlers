plugins {
    id("java")
    id("de.eldoria.plugin-yml.bukkit") version "0.7.1"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "com.github.supersimple33"
version = "0.2.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    compileTestJava {
        options.encoding = "UTF-8"
    }

    javadoc {
        options.encoding = "UTF-8"
    }

    runServer {
        minecraftVersion("1.21.4")
    }
}

tasks.withType(xyz.jpenilla.runtask.task.AbstractRun::class) {
    javaLauncher = javaToolchains.launcherFor {
        vendor = JvmVendorSpec.JETBRAINS
        languageVersion = JavaLanguageVersion.of(21)
    }
    jvmArgs("-XX:+AllowEnhancedClassRedefinition")
}

tasks.test {
    useJUnitPlatform()
}

bukkit {
    name = "Howlers"
    author = "supersimple33"
    description = "This plugin sets wolves to howl at the moon"

    apiVersion = "1.17"
    main = "com.github.supersimple33.howlers.Woof"

    permissions {
        register("howlers.reload") {
            description = "Allows the player to reload the plugin"
        }
    }

    commands {
        register("hwreload") {
            description = "This reloads the config of the howlers plugin"
            usage = "/hwreload"
            permission = "howlers.reload"
            permissionMessage = "You do not have permission to use this command."
        }
    }
}

