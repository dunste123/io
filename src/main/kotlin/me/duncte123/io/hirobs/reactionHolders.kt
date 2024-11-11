package me.duncte123.io.hirobs

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction

class StringReaction(
    triggers: List<String>,
    response: (MessageReceivedEvent) -> String,
    probability: Double,
    hasCool: Boolean,
    passesFor: (MessageReceivedEvent) -> Boolean = { true }
): MessageActionReaction(
    triggers,
    { event, action -> action.setContent(response(event)) },
    probability,
    hasCool,
    passesFor
)

class RegexReaction(
    triggers: List<String>,
    response: (MessageReceivedEvent) -> String,
    probability: Double,
    hasCool: Boolean,
    passesFor: (List<String>, MessageReceivedEvent) -> Boolean = {_, _  -> true }
): MessageActionReaction(
    triggers,
    { event, action -> action.setContent(response(event)) },
    probability,
    hasCool,
    { passesFor(triggers, it) }
)

open class MessageActionReaction(
    val triggers: List<String>,
    val response: (MessageReceivedEvent, MessageCreateAction) -> MessageCreateAction,
    val probability: Double,
    val hasCool: Boolean,
    val passesFor: (MessageReceivedEvent) -> Boolean = { true }
)
