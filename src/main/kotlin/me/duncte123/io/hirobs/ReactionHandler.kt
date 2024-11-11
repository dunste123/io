package me.duncte123.io.hirobs

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Bucket4j
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.entities.sticker.StickerSnowflake
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.time.Duration
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.ThreadLocalRandom

const val GEN_CHAT_ID = 512711766845947924L

/**
 * Hello nerd! As you may know, Hiro no longer does these reactions, but that does not mean he's silent ;)
 * Hiro has a bunch of new stuff added, have fun finding it!
 */
class ReactionHandler {
    // latest will always be the message we are checking the time against
    private var latestGenMessageTime = OffsetDateTime.now()
    private var previousGenMessageTime = OffsetDateTime.now()

    // The limit is one reaction per 300 seconds or 5 minutes
    private val limit = Bandwidth.simple(1, Duration.ofSeconds(300))
    private val buckets = mutableMapOf<String, Bucket>()

    private val ioRegex = ":(?:[^:]+)?io[^:]+?:".toRegex()

    // if you read this, the key is used for ratelimiting
    private val reactionMap = mapOf(
        "im" to RegexReaction(
            listOf("i'm", "im"),
            { event ->
                val regex = "\\bi'?m ([^<:.,;)!?\n]+)".toRegex(RegexOption.IGNORE_CASE)
                val name = regex.find(event.message.contentRaw)
                val groups = name!!.groups
                val target = groups.last()!!.value.trim()
                val self = event.guild.selfMember.effectiveName

                if (target.lowercase() == self.lowercase()) {
                    return@RegexReaction "No you're not!"
                }

                val targetYou = target
                    .replace("\\bi am\\b".toRegex(RegexOption.IGNORE_CASE), "you are")
                    .replace("\\bi'?m\\b".toRegex(RegexOption.IGNORE_CASE), "you're")
                    .replace("\\bi'?ve\\b".toRegex(RegexOption.IGNORE_CASE), "you have")
                    .replace("\\bi\\b".toRegex(RegexOption.IGNORE_CASE), "you")
                    .replace("\\bmy\\b".toRegex(RegexOption.IGNORE_CASE), "your")

                return@RegexReaction "Hi $targetYou. I'm $self"
            },
            5.0,
            true,
            {
                    triggers, event ->
                triggers.any {
                    "\\b$it ([^<:.,]+)".toRegex(RegexOption.IGNORE_CASE)
                        .find(event.message.contentRaw) != null
                }
            }
        ),
        "io" to StringReaction(
            listOf("io", "<@1215363445583646750>", "<@!1215363445583646750>"),
            { "IO? That's me." },
            5.0,
            true,
            {
                ioRegex.find(it.message.contentRaw.lowercase()) == null ||
                        it.message.contentRaw.contains("<@!?1215363445583646750>".toRegex())
            }
        ),
        "oldlady" to StringReaction(
            listOf("old lady"),
            {
                "```diff\n--- changelog of ${it.message.timeCreated}\n- Old lady has been removed from the game\n+ Ms. Saki has been added to the game\n```"
            },
            10.0,
            true
        ),
        "gays" to StringReaction(
            listOf("gays"),
            { "Of course gays, what did you expect... This is a BL game fan server." },
            5.0,
            true
        ),
        "smh" to StringReaction(
            listOf("smh"),
            { "smh my " + listOf(
                "head", "skateboard", "eiffeltower", "sonic screwdriver", "di-", "baseball", "Shakespeare", "studio", "diriger",
                "husband", "charger", "Yuuto", "giacomino, guardiano delle galassie e dell'iperspazio"
            ).random() },
            5.0,
            true
        ),
        "frog" to StringReaction(
            listOf("frog"),
            { "Hippity Hoppity \uD83D\uDC38" },
            20.0,
            true
        ),
        "blits" to StringReaction(
            listOf("blits"),
            { "BLits, it's BL. <:BLitsLogo:689216149216428071>" },
            5.0,
            true
        ),
        // https://discord.com/channels/512711766845947922/512711766845947924/674314232308957194
        "lewd" to MessageActionReaction(
            listOf("lewd"),
            { _, action ->
                action.setStickers(StickerSnowflake.fromId(865278553406701568L))
            },
            20.69,
            true,
            { it.channel.idLong == GEN_CHAT_ID }
        ),
    )

    private val channelReactions = mapOf(
        GEN_CHAT_ID to StringReaction(
            listOf("deadchat", "dead chat", "dead server", "ded chat", "ichirudeadchat"),
            { randomDeadChat().replace("_", "\\_") },
            80.0,
            true,
            {
                // check if the last message was 20+ minutes ago
                previousGenMessageTime.until(it.message.timeCreated, ChronoUnit.MINUTES) >= 20
            }
        ),
    )

    private fun getChannelReaction(channelId: Long, lower: String, rand: Double): Map.Entry<String, MessageActionReaction>? {
        if (channelId !in channelReactions) {
            return null
        }

        val reaction = channelReactions[channelId] ?: return null
        // Empty triggers count :)
        val hasTrigger = reaction.triggers.any { t -> t.isEmpty() || lower.contains(t) }

        // rand is less or equal to pb, means that it will trigger only pb% of the time
        if (hasTrigger && rand <= reaction.probability) {
            val key = reaction.triggers.firstOrNull() ?: channelId.toString()
            return HiroEntry(key, reaction)
        }

        return null
    }

    private fun getReaction(event: MessageReceivedEvent): Map.Entry<String, MessageActionReaction>? {
        if (event.channelType != ChannelType.TEXT) {
            return null
        }

        if (!event.channel.asGuildMessageChannel().canTalk()) {
            return null
        }

        val lower = event.message.contentRaw.lowercase()
        val rand = ThreadLocalRandom.current().nextDouble(101.0)

        // start with trying the text channel reactions
        return getChannelReaction(event.channel.idLong, lower, rand)
            ?: return reactionMap.entries.find {
                (lower.contains(it.key) || it.value.triggers.find { t -> lower.contains(t) } != null) &&
                        rand <= it.value.probability
            }
    }

    fun getReactionIfNotLimited(event: MessageReceivedEvent): MessageActionReaction? {
        val reaction = getReaction(event) ?: return null

        if (!reaction.value.passesFor(event)) {
            return null
        }

        // If there is no cooldown we can just return the Reaction
        if (!reaction.value.hasCool) {
            return reaction.value
        }

        val bucket = buckets.getOrPut(reaction.key) { Bucket4j.builder().addLimit(limit).build() }

        // try and consume the rate limit
        if (bucket.tryConsume(1)) {
            return reaction.value
        }

        return null
    }

    fun updateMessageTime(time: OffsetDateTime) {
        this.previousGenMessageTime = this.latestGenMessageTime
        this.latestGenMessageTime = time
    }

    private class HiroEntry<K, V>(override val key: K, override val value: V) : Map.Entry<K, V>
}
