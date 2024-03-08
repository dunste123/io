package me.duncte123.io.commands

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import me.duncte123.io.ICommand
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserCommand @JsonCreator constructor (
    @JsonProperty("disabled") val disabled: Boolean = false,
    @JsonProperty("name") override val name: String,
    @JsonProperty("description") override val description: String,
    @JsonProperty("input") override val options: List<OptionData> = listOf(),
    @JsonProperty("output") val output: String,
) : ICommand {
    override fun execute(event: SlashCommandInteractionEvent) {
        event.reply(output).queue()
    }
}
