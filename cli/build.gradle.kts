plugins {
    id("jacoco")
    id("printscript.v1.kotlin-application-conventions")
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
