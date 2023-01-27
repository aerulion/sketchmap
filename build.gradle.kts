plugins {
    `java-library`
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
    id("maven-publish")
}

group = "net.aerulion"
version = "2.0.0"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:23.1.0")
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }
}

bukkit {
    name = "SketchMap"
    main = "net.aerulion.sketchmap.SketchMapPlugin"
    version = getVersion().toString()
    author = "aerulion"
    apiVersion = "1.19"
    commands {
        register("sketchmap") {
            description = "SketchMap Core Command"
        }
    }
}