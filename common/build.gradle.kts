plugins {
    id("printscript.common-conventions")
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.1")
    implementation("org.yaml:snakeyaml:2.1")
    // testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.8.10")
    testImplementation(kotlin("test"))
}

sourceSets {
    test {
        resources {
            srcDir("src/test/resources")
            include("config.json")
        }
    }
}
