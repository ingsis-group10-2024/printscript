dependencies {
    implementation(project(":common"))
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation(project(":common"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0")
}
