package me.duncte123.io.commands

import me.duncte123.io.ICommand
import me.duncte123.io.commandManager
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.build.CommandData

class ReloadCommand : ICommand {
    override val name = "reload"
    override val description = "Reloads all user commands"

    override fun execute(event: SlashCommandInteractionEvent) {
        commandManager.resetCommands()
        commandManager.registerUserCommands()
        commandManager.registerCommandsOnJda(event.jda)

        event.reply("Reloading commands!").queue()
    }

    override fun toCommandData(): CommandData {
        return super.toCommandData()
            .setDefaultPermissions(
                DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)
            )
    }
}
