plugins {
    kotlin("jvm") version "1.9.21"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
    id("maven-publish")
}

group = "com.example"
version = "1.0.0-SNAPSHOT" // -snapshot para que sea una versión pisable

repositories {
    mavenCentral()
}

subprojects {
    println("Configuring subproject: $name")
    apply {
        plugin("org.jlleitschuh.gradle.ktlint")
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

ktlint {
    filter {
        include("**/kotlin/**")
        exclude("**/generated/**")
    }
}

tasks.register<Copy>("copyPreCommitHook") {
    from(File(rootProject.rootDir, "scripts/pre-commit"))
    into(File(rootProject.rootDir, ".git/hooks"))
    fileMode = 777
}

tasks.getByName("build").dependsOn("copyPreCommitHook")

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/ingsis-group10-2024/printscript")
            version = "1.0.0-SNAPSHOT"
            credentials {
                username = System.getenv("GITHUB_USERNAME") ?: ""
                password = System.getenv("GITHUB_TOKEN") ?: ""
            }
        }
    }
}
