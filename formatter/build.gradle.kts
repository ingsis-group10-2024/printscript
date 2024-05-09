plugins {
    jacoco
}
dependencies {
    implementation(project(":common"))
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation(project(":common"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0")
}
repositories {
    mavenCentral()
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
