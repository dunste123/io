package me.duncte123.io

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionContextType
import net.dv8tion.jda.api.interactions.commands.build.*

interface ICommand {
    val name: String
    val description: String

    val options: List<OptionData>
        get() = listOf()
    val subCommands: List<SubcommandData>
        get() = listOf()

    fun execute(event: SlashCommandInteractionEvent)

    fun toCommandData(): CommandData {
        val data = Commands.slash(name, description)
            .setContexts(InteractionContextType.GUILD)
//            .setNSFW(true)

        if (options.isNotEmpty()) {
            data.addOptions(options)
        }

        if (subCommands.isNotEmpty()) {
            data.addSubcommands(subCommands)
        }

        return data
    }
}
