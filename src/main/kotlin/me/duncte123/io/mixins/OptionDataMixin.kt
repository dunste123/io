package me.duncte123.io.mixins

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSetter
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

abstract class OptionDataMixin @JsonCreator constructor(
    @JsonProperty("type") type: OptionType,
    @JsonProperty("name") name: String,
    @JsonProperty("desc") desc: String,
    @JsonProperty("required") required: Boolean
) {
    @JsonIgnore
    abstract fun setChannelTypes(vararg channelTypes: ChannelType): OptionData

    @JsonIgnore
    abstract fun setMinValue(minValue: Double): OptionData

    @JsonIgnore
    abstract fun setMaxValue(minValue: Double): OptionData

    @JsonSetter("choices")
    abstract fun addChoices(choices: Collection<Command.Choice>): OptionData
}
