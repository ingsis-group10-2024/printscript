plugins {
    id("printscript.common-conventions")
}

dependencies {
    implementation(project(":common"))
    implementation("org.apache.pdfbox:pdfbox:2.0.29")
}
tasks.test {
    // Configura el tamaño inicial y máximo del heap
    minHeapSize = "5m" // Tamaño inicial del heap
    maxHeapSize = "7m" // Tamaño máximo del heap

    // Otras configuraciones opcionales
    useJUnitPlatform() // Usa JUnit Platform para ejecutar las pruebas
}
