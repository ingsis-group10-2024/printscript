plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "printscript"
include("cli")
include("common")
include("formatter")
include("interpreter")
include("lexer")
include("linter")
include("parser")
include("sca")