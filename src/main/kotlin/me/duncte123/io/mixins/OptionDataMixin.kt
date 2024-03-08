package me.duncte123.io.mixins

import com.fasterxml.jackson.annotation.JsonIgnore
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

abstract class OptionDataMixin {
    @JsonIgnore
    abstract fun setChannelTypes(vararg channelTypes: ChannelType): OptionData

    @JsonIgnore
    abstract fun setMinValue(minValue: Double): OptionData

    @JsonIgnore
    abstract fun setMaxValue(minValue: Double): OptionData
}
