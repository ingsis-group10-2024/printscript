package config

import com.fasterxml.jackson.annotation.JsonProperty

data class ConfigRule(
    @JsonProperty("name") val name: String,
    @JsonProperty("enabled") val enabled: Boolean,
)
