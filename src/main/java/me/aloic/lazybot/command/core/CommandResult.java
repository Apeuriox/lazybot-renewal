package me.aloic.lazybot.command.core;

import java.util.List;

/** Platform-neutral command output. Platform adapters decide how to deliver it. */
public sealed interface CommandResult permits CommandResult.Text, CommandResult.Image,
        CommandResult.Composite, CommandResult.Empty, CommandResult.LegacySideEffect {

    String getType();

    record Text(String content) implements CommandResult {
        @Override
        public String getType() {
            return "TEXT";
        }
    }

    record Image(byte[] content, String contentType, String fileName, String caption) implements CommandResult {
        public Image {
            content = content == null ? new byte[0] : content.clone();
        }

        @Override
        public byte[] content() {
            return content.clone();
        }

        @Override
        public String getType() {
            return "IMAGE";
        }
    }

    record Composite(List<CommandResult> results) implements CommandResult {
        public Composite {
            results = List.copyOf(results);
        }

        @Override
        public String getType() {
            return "COMPOSITE";
        }
    }

    record Empty() implements CommandResult {
        @Override
        public String getType() {
            return "EMPTY";
        }
    }

    /** Temporary response used while a legacy command still writes through TestOutputTool. */
    record LegacySideEffect(String message) implements CommandResult {
        @Override
        public String getType() {
            return "LEGACY_SIDE_EFFECT";
        }
    }
}
