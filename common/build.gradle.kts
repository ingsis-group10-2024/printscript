plugins {
    id("jacoco")
    java
    id("maven-publish")
}

dependencies {
    implementation("junit:junit:4.13.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.5")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.5")
    implementation("org.yaml:snakeyaml:1.28")
}
sourceSets {
    test {
        resources {
            srcDir("src/test/resources")
            include("config.json")
        }
    }
}

repositories {
    mavenCentral()
}

jacoco {
    toolVersion = "0.8.11"
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}
tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}

jacoco {
    toolVersion = "0.8.11"
    layout.buildDirectory.dir("customJacocoReportDir")
}

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
            version = "1.0.1-SNAPSHOT"
            credentials {
                username = System.getenv("GITHUB_USERNAME") ?: ""
                password = System.getenv("GITHUB_TOKEN") ?: ""
            }
        }
    }
}
