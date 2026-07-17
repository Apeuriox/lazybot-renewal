package me.aloic.lazybot.command.core;

import me.aloic.lazybot.exception.LazybotRuntimeException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/** Platform-neutral arguments preserving both text positionals and structured named values. */
public record CommandArguments(List<String> positional, Map<String, Object> named) {
    public CommandArguments {
        positional = List.copyOf(positional);
        Map<String, Object> normalized = new LinkedHashMap<>();
        named.forEach((key, value) -> normalized.put(key.toLowerCase(Locale.ROOT), value));
        named = Map.copyOf(normalized);
    }

    public static CommandArguments positional(List<String> positional) {
        return new CommandArguments(positional, Map.of());
    }

    public static CommandArguments named(Map<String, Object> named) {
        return new CommandArguments(List.of(), named);
    }

    public Optional<String> string(String name) {
        Object value = named.get(normalizeName(name));
        return value == null ? Optional.empty() : Optional.of(String.valueOf(value));
    }

    public Optional<Integer> integer(String name) {
        Object value = named.get(normalizeName(name));
        if (value == null) {
            return Optional.empty();
        }
        if (value instanceof Number number) {
            return Optional.of(Math.toIntExact(number.longValue()));
        }
        try {
            return Optional.of(Integer.parseInt(String.valueOf(value)));
        }
        catch (NumberFormatException e) {
            throw new LazybotRuntimeException("参数 " + name + " 必须是整数");
        }
    }

    public boolean hasNamed(String name) {
        return named.containsKey(normalizeName(name));
    }

    private static String normalizeName(String name) {
        return name.toLowerCase(Locale.ROOT);
    }
}
