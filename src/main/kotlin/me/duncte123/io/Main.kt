package me.duncte123.io

import me.duncte123.io.commands.JjCommand
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.hooks.EventListener

val commands = mapOf(
    "jj" to JjCommand()
)

fun main() {
    JDABuilder.createLight(System.getenv("BOT_TOKEN"))
        .addEventListeners(object : EventListener {
            override fun onEvent(event: GenericEvent) {
                if (event is ReadyEvent) {
                    event.jda.updateCommands()
                        .addCommands(commands.values.map { it.toCommandData() })
                        .queue()
                } else if (event is SlashCommandInteractionEvent) {
                    commands[event.name]?.execute(event)
                }
            }
        })
        .build()
}
