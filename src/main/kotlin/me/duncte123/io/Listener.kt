package me.duncte123.io

import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.hooks.EventListener

class Listener : EventListener {
    override fun onEvent(event: GenericEvent) {
        when (event) {
            is ReadyEvent -> onReady(event)
            is SlashCommandInteractionEvent -> onSlashCommandInteraction(event)
            is MessageReceivedEvent -> {
                if (event.isFromType(ChannelType.PRIVATE)) {
                    event.message
                        .reply("Hey you! Wishlist jock studio now! <https://store.steampowered.com/app/2115310/Jock_Studio?utm_source=io_bot>")
                        .queue()
                }
            }
        }
    }

    private fun onReady(event: ReadyEvent) {
        commandManager.registerCommandsOnJda(event.jda)
    }

    private fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        commandManager.handleCommand(event)
    }
}
