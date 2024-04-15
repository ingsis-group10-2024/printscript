package config

import com.fasterxml.jackson.annotation.JsonProperty

data class ConfigRule(
    @JsonProperty("name") val name: String,
    @JsonProperty("enabled") val enabled: Boolean,
    @JsonProperty("value") val value: Int? = null // Valor opcional
) {
    // Constructor alternativo para cuando tenemos valores numericos
    constructor(name: String, value: Int) : this(name, true, value)
}
