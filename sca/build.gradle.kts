dependencies {
    implementation(project(":common"))
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.0")
    implementation("com.fasterxml.jackson.core:jackson-core:2.13.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.13.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.0")
}

repositories {
    mavenCentral()
}
