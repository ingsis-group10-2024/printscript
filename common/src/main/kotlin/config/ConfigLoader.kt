package config

interface ConfigLoader {
    fun loadConfig(): VerificationConfig
}
