package me.duncte123.io.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

class OptionDataDeserializer(vc: Class<*>? = null) : StdDeserializer<OptionData>(vc) {

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): OptionData {
        val node = p.codec.readTree<JsonNode>(p)

        return OptionData(
            OptionType.valueOf(node.get("type").textValue()),
            node.get("name").textValue(),
            node.get("desc").textValue(),
            node.get("required").booleanValue()
        )
    }
}
