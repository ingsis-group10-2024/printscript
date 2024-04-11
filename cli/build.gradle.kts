
plugins {
    id("jacoco")
}
dependencies {
    implementation(project(":formatter"))
    implementation(project(":lexer"))
    implementation(project(":parser"))
    implementation(project(":interpreter"))
    implementation(project(":linter"))
    implementation("com.github.ajalt.clikt:clikt:4.3.0")
}

repositories {
    mavenCentral()
}
