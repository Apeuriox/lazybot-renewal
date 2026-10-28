package me.aloic.lazybot.osu.wiki;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class ModModeSymbols
{
    private final String markup;

    public ModModeSymbols()
    {
        this.markup = load();
    }

    public String markup()
    {
        return markup;
    }

    private static String load()
    {
        ClassPathResource resource = new ClassPathResource("static/mods/mode-icons.svg");
        try (InputStream in = resource.getInputStream()) {
            String raw = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            int open = raw.indexOf('>');
            int close = raw.lastIndexOf("</svg>");
            if (open < 0 || close < 0 || close <= open) {
                throw new IllegalStateException("mode-icons.svg 格式不对");
            }
            return raw.substring(open + 1, close).trim();
        }
        catch (Exception e) {
            throw new IllegalStateException("无法读取模式图标", e);
        }
    }
}
