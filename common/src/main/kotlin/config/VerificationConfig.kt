package config

import com.fasterxml.jackson.annotation.JsonProperty

data class VerificationConfig(
    @JsonProperty("activeRules")
    val activeRules: List<ConfigRule>
)