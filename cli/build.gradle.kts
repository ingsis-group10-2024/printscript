plugins {
    id("jacoco")
    id("maven-publish")
}
dependencies {
    implementation(project(":common"))
    implementation(project(":formatter"))
    implementation(project(":lexer"))
    implementation(project(":parser"))
    implementation(project(":interpreter"))
    implementation(project(":linter"))
    implementation(project(":sca"))
    implementation("com.github.ajalt.clikt:clikt:4.3.0")
}

repositories {
    mavenCentral()
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
