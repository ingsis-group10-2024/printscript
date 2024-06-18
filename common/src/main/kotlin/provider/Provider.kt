package provider

interface Provider {
    fun provide(value: String): String
}
