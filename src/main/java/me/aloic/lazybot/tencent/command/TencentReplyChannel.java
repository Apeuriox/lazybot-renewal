package me.aloic.lazybot.tencent.command;

import me.aloic.lazybot.command.CommandReply;
import me.aloic.lazybot.tencent.api.TencentOpenApiClient;
import me.aloic.lazybot.tencent.event.TencentIncomingMessage;

import java.util.concurrent.atomic.AtomicInteger;

public class TencentReplyChannel implements CommandReply
{
    private final TencentOpenApiClient apiClient;
    private final TencentIncomingMessage incoming;
    private final AtomicInteger sequence = new AtomicInteger(1);

    public TencentReplyChannel(TencentOpenApiClient apiClient, TencentIncomingMessage incoming)
    {
        this.apiClient = apiClient;
        this.incoming = incoming;
    }

    @Override
    public void sendText(String message)
    {
        apiClient.sendText(
                incoming.getScene(),
                incoming.targetOpenid(),
                incoming.getMessageId(),
                sequence.getAndIncrement(),
                message);
    }

    @Override
    public void sendImage(byte[] imageBytes)
    {
        apiClient.sendImage(
                incoming.getScene(),
                incoming.targetOpenid(),
                incoming.getMessageId(),
                sequence.getAndIncrement(),
                imageBytes);
    }
}
