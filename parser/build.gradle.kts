plugins {
    id("jacoco")
}

dependencies{
    implementation(project(":common"))
    testImplementation("org.jetbrains.kotlin:kotlin-test")
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




/*
tasks.jacocoTestReport{
    dependsOn("test")
    reports {
        xml.required = true
        html.required = true
    }
}


 */