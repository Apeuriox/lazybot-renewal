package me.aloic.lazybot.command;

/**
 * Platform-agnostic outbound replies for a single incoming command.
 * Used by the Tencent bot (and potentially later platforms) so commands
 * do not depend on Shiro {@code Bot} or JDA events.
 */
public interface CommandReply
{
    void sendText(String message);

    void sendImage(byte[] imageBytes);

    default void sendTextWithImage(String text, byte[] imageBytes)
    {
        if (text != null && !text.isBlank()) {
            sendText(text);
        }
        if (imageBytes != null && imageBytes.length > 0) {
            sendImage(imageBytes);
        }
    }
}
