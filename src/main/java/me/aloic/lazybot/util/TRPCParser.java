package me.aloic.lazybot.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TRPCParser
{
    public static String parsetRPCJson(String unhandled) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode outer = mapper.readTree(unhandled);
        String inner = outer.get(0).get("result").get("data").asText();

        String json = inner
                .replaceAll("new Date\\((\\d+)\\)", "\"$1\"")
                .replaceAll("void 0", "null")
                .replaceAll("([0-9]+)n", "$1")
                .replaceAll(":'([^']*)'", ":\"$1\"")
                .replaceAll("(?<=\\{|,)(\\s*)([a-zA-Z0-9_]+)\\s*:", "$1\"$2\":")
                .replaceAll("\\\\'", "'");


        JsonNode data = mapper.readTree(json);

       return (mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data));
    }

}
