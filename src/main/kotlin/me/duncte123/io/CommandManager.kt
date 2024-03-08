package me.duncte123.io

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import me.duncte123.io.commands.JjCommand
import me.duncte123.io.commands.ReloadCommand
import me.duncte123.io.commands.UserCommand
import me.duncte123.io.jackson.OptionDataDeserializer
import me.duncte123.io.mixins.OptionDataMixin
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.slf4j.LoggerFactory
import java.io.File

class CommandManager {
    private val log = LoggerFactory.getLogger(CommandManager::class.java)
    private val jackson = ObjectMapper(YAMLFactory())
        .addMixIn(OptionData::class.java, OptionDataMixin::class.java)

    private val commands = mutableMapOf<String, ICommand>()

    init {
        val module = SimpleModule()

        module.addDeserializer(OptionData::class.java, OptionDataDeserializer())

        jackson.registerModule(module)

        resetCommands()
    }

    fun resetCommands() {
        commands.clear()

        commands.putAll(getDefaultCommands())
    }

    private fun getDefaultCommands() = mapOf(
        "jj" to JjCommand(),
        "reload" to ReloadCommand()
    )

    private fun getAllCommandData() = commands.values.map { it.toCommandData() }

    fun handleCommand(event: SlashCommandInteractionEvent) {
        commands[event.name]?.execute(event)
    }

    fun registerCommandsOnJda(jda: JDA) {
        jda.updateCommands()
            .addCommands(getAllCommandData())
            .queue()
    }

    fun registerUserCommands() {
        val userCommands = getUserCommands()

        userCommands.forEach {
            if (!it.disabled) {
                commands[it.name] = it
                log.info("Registered user command: {}", it.name)
            }
        }
    }

    private fun getUserCommands(): List<UserCommand> {
        val cmds = mutableListOf<UserCommand>()

        collectFileCommands().forEach {
            cmds.add(jackson.readValue(it, UserCommand::class.java))
        }

        return cmds
    }

    private fun collectFileCommands() = File("commands").listFiles()!!.filter { it.extension == "yml" }
}
