package me.duncte123.io

import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

val commandManager = CommandManager()

fun main() {
    commandManager.registerUserCommands()

    val jda = JDABuilder.createLight(System.getenv("BOT_TOKEN"))
        .addEventListeners(Listener())
        .setActivity(Activity.customStatus("SOURCE CODE IN BIO"))
        .build()

    val timer = Executors.newSingleThreadScheduledExecutor {
        Thread(it).apply {
            name = "Status updater"
            isDaemon = true
        }
    }

    timer.scheduleAtFixedRate(
        {
            jda.presence.setPresence(
                Activity.customStatus("SOURCE CODE IN BIO"),
                false
            )
        },
        1L, 1L, TimeUnit.DAYS
    )
}
