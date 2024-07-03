plugins{
    id("printscript.common-conventions")
}

dependencies {
    implementation(project(":common"))
    implementation("org.apache.pdfbox:pdfbox:2.0.29")
}
