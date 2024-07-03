plugins {
    id("printscript.app-conventions")
}

dependencies {
    implementation(project(":common"))
    implementation(project(":formatter"))
    implementation(project(":lexer"))
    implementation(project(":parser"))
    implementation(project(":interpreter"))
    implementation(project(":linter"))
    implementation(project(":sca"))
}
