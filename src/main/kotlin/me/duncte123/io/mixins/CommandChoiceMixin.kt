package me.duncte123.io.mixins

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

abstract class CommandChoiceMixin
    @JsonCreator
    constructor(
        @JsonProperty("name") name: String,
        @JsonProperty("value") value: String,
    ) {
}
