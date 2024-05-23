plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "printscript"
include("linter")
include("formatter")
include("interpreter")
include("parser")
include("lexer")
include("common")
include("cli")
include("sca")
