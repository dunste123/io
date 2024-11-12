package me.duncte123.io

import me.duncte123.io.hirobs.GEN_CHAT_ID
import me.duncte123.io.hirobs.MessageStackReactor
import me.duncte123.io.hirobs.ReactionHandler
import net.dv8tion.jda.api.entities.Message.MentionType
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.internal.requests.restaction.MessageCreateActionImpl
import java.util.*

class Listener : EventListener {
    private val reactionHandler = ReactionHandler()
    private val messageStacker = MessageStackReactor()

    override fun onEvent(event: GenericEvent) {
        when (event) {
            is ReadyEvent -> onReady(event)
            is SlashCommandInteractionEvent -> onSlashCommandInteraction(event)
            is MessageReceivedEvent -> onMessageReceived(event)
        }
    }

    private fun onReady(event: ReadyEvent) {
        commandManager.registerCommandsOnJda(event.jda)
    }

    private fun onMessageReceived(event: MessageReceivedEvent) {
        // ALWAYS IGNORE BOTS
        if (event.author.isBot) {
            return
        }

        if (event.isFromType(ChannelType.PRIVATE)) {
            event.message
                .reply("Hey you! Wishlist jock studio now! <https://store.steampowered.com/app/2115310/Jock_Studio?utm_source=io_bot>")
                .queue()

            return
        }


        if (event.channel.idLong == GEN_CHAT_ID) {
            reactionHandler.updateMessageTime(event.message.timeCreated)
        }

        // Reactions for everyone :D
        val reaction = reactionHandler.getReactionIfNotLimited(event)

        if (reaction !== null) {
            // message id must be null, triggers edit endpoint otherwise
            reaction.response(event, MessageCreateActionImpl(event.channel))
                .setMessageReference(event.message)
                .mentionRepliedUser(true)
                .setAllowedMentions(EnumSet.allOf(MentionType::class.java))
                .queue()
            return
        }

        messageStacker.handleMessageEvent(event)
    }

    private fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        commandManager.handleCommand(event)
    }
}
