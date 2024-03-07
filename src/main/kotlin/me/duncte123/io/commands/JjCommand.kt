package me.duncte123.io.commands

import me.duncte123.io.ICommand
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.utils.FileUpload

const val JJ_ID = 272258147169599489L

class JjCommand : ICommand {
    override val name = "jj"
    override val description = "Allows JJ to upload pictures via the bot."

    override val options = listOf(
        OptionData(
            OptionType.CHANNEL,
            "channel",
            "Where to send the file?",
            true
        ),
        OptionData(
            OptionType.ATTACHMENT,
            "file",
            "the file to send to the channel",
            true
        )
    )

    override fun execute(event: SlashCommandInteractionEvent) {
        if (event.user.idLong != JJ_ID) {
            event.reply("you are not jj").setEphemeral(true).queue()
            return
        }

        val channel = event.getOption("channel")!!.asChannel

        if (!channel.type.isMessage) {
            event.reply("Must be a text channel fuckwhat").setEphemeral(true).queue()
            return
        }

        val msgChan = channel.asGuildMessageChannel()

        if (!msgChan.canTalk()) {
            event.reply("Can't talk there :)").setEphemeral(true).queue()
            return
        }

        val file = event.getOption("file")!!.asAttachment

        event.reply("Sending the file to ${channel.asMention}").queue()

        file.proxy.download().thenAccept { stream ->
            msgChan.sendFiles(FileUpload.fromData(stream, file.fileName))
                .queue()
        }


    }
}
