package me.duncte123.io.hirobs

import net.dv8tion.jda.api.entities.Message.MentionType
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.jodah.expiringmap.ExpiringMap
import java.util.*
import java.util.concurrent.TimeUnit

const val MIN_STACK_REPLY = 3

class MessageStackReactor {
    // Message data storage
    // channel -> data (don't need more than 1 per channel)

    // Each channel can only have one stack, because we're checking for repeated messages in the same channel.
    private val dataMap = ExpiringMap.builder()
        .expiration(30, TimeUnit.SECONDS)
        .build<Long, MessageInfo>()

    fun handleMessageEvent(event: MessageReceivedEvent) {
        val channelId = event.channel.idLong
        val userId = event.author.idLong

        var data = dataMap[channelId]
        val currContent = event.message.contentRaw

        if (data == null) {
            dataMap[channelId] = MessageInfo(currContent, listOf(userId))
            return
        }

        if (data.isMatch(currContent) && userId !in data.posters) {
            data = data.increment(userId)

            if (data.count >= MIN_STACK_REPLY) {
                event.channel
                    .sendMessage(currContent)
                    .setAllowedMentions(EnumSet.allOf(MentionType::class.java))
                    .queue()
                dataMap.remove(channelId)

                return
            }

            dataMap[channelId] = data
            return
        }

        // we have a new message
        dataMap[channelId] = MessageInfo(currContent, listOf(userId))
    }

    data class MessageInfo(val message: String, val posters: List<Long>, val count: Int = 1) {
        fun increment(userId: Long) = copy(count = count + 1, posters = posters + userId)

        fun isMatch(content: String): Boolean = content.lowercase() == message.lowercase()
    }
}
