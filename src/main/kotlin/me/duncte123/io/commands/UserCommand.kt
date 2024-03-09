package me.duncte123.io.commands

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import me.duncte123.io.ICommand
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.requests.RestAction
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
import javax.script.SimpleBindings
import kotlin.math.floor
import kotlin.math.min

private val kotlinEngine: ScriptEngine by lazy {
    ScriptEngineManager().getEngineByExtension("kts")!!.apply {
        this.eval(
            """
            import java.io.*
            import java.lang.*
            import java.math.*
            import java.time.*
            import java.util.*
            import java.util.concurrent.*
            import java.util.stream.*
            import net.dv8tion.jda.api.*
            import net.dv8tion.jda.api.entities.*
            import net.dv8tion.jda.api.entities.channel.*
            import net.dv8tion.jda.api.entities.channel.attribute.*
            import net.dv8tion.jda.api.entities.channel.middleman.*
            import net.dv8tion.jda.api.entities.channel.concrete.*
            import net.dv8tion.jda.api.sharding.*
            import net.dv8tion.jda.internal.entities.*
            import net.dv8tion.jda.api.managers.*
            import net.dv8tion.jda.internal.managers.*
            import net.dv8tion.jda.api.utils.*
            """.trimIndent()
        )
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserCommand @JsonCreator constructor (
    @JsonProperty("disabled") val disabled: Boolean = false,
    @JsonProperty("name") override val name: String,
    @JsonProperty("description") override val description: String,
    @JsonProperty("input") override val options: List<OptionData> = listOf(),
    @JsonProperty("output") val output: String,
) : ICommand {
    override fun execute(event: SlashCommandInteractionEvent) {
        event.deferReply(false).queue()

        val bindings = SimpleBindings()

        bindings["event"] = event

        val response = try {
            kotlinEngine.eval(output, bindings)
        } catch (e: Exception) {
            e
        }

        parseEvalResponse(response, event)
    }

    private fun parseEvalResponse(out: Any?, event: SlashCommandInteractionEvent) {
        val send: (message: String) -> Unit = { event.hook.sendMessage(it).queue() }

        when (out) {
            null -> send("(command produced no output)")

            is Throwable -> {
                send("ERROR: $out")
            }

            is RestAction<*> -> {
                out.queue({
                    send("Rest action success: $it")
                }) {
                    send("Rest action error: $it")
                }
            }

            is MessageEmbed -> {



                val leftUser = event.getOption("user1")?.asMember!!
                val rightUser = event.getOption("user2")?.asMember ?: event.member!!

                event.hook.sendMessageEmbeds(out).queue()
            }

            else -> {
                val toString = out.toString()

                if (toString.isEmpty() || toString.isBlank()) {
                    send("(command produced no output)")
                    return
                }

                send(toString.truncate(1900))
            }
        }
    }

    private fun String.truncate(length: Int): String {
        if (this.length < length) {
            return this
        }

        return this.substring(0, min(this.length, length - 3)) + "..."
    }
}
