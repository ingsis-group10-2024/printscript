plugins {
    kotlin("jvm") version "1.9.21"
 //   id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

subprojects {
    println("Configuring subproject: $name")
    apply {
        //TODO: Actualizar el codigo a las condiciones de este linter.
       //plugin("org.jlleitschuh.gradle.ktlint")
        plugin("org.jetbrains.kotlin.jvm")
    }
    dependencies {
        implementation(kotlin("stdlib"))
        testImplementation("org.jetbrains.kotlin:kotlin-test:1.8.10")
        testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.8.10")
    }
    repositories {
        mavenCentral()
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.8.10")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.8.10")
}

tasks.test {
    useJUnit()
}
