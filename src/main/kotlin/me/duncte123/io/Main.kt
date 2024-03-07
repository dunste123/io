package me.duncte123.io

import net.dv8tion.jda.api.JDABuilder

fun main() {
    JDABuilder.createLight(System.getenv("BOT_TOKEN")).build()
}
