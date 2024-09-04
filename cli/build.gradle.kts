plugins {
    id("application")
    id("printscript.common-conventions")
}

dependencies {

    implementation("com.github.ajalt.clikt:clikt:4.3.0")

    implementation(project(":common"))
    implementation(project(":formatter"))
    implementation(project(":lexer"))
    implementation(project(":parser"))
    implementation(project(":interpreter"))
    implementation(project(":linter"))
    implementation(project(":sca"))
}
