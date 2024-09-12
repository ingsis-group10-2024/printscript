plugins {
    id("printscript.common-conventions")
}

dependencies {
    implementation(project(":common"))
    implementation("com.google.code.gson:gson:2.8.9")
    testImplementation("junit:junit:4.13.2")
}
