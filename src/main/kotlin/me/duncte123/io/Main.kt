package me.duncte123.io

import net.dv8tion.jda.api.JDABuilder

val commandManager = CommandManager()

fun main() {
    commandManager.registerUserCommands()

    JDABuilder.createLight(System.getenv("BOT_TOKEN"))
        .addEventListeners(Listener())
        .build()
}
