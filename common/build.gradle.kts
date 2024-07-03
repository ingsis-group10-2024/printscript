plugins {
    id("printscript.common-conventions")
}

sourceSets {
    test {
        resources {
            srcDir("src/test/resources")
            include("config.json")
        }
    }
}
