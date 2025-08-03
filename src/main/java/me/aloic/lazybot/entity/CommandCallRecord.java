package me.aloic.lazybot.entity;

import java.time.LocalDateTime;

public record CommandCallRecord(String userId, String channelId, LocalDateTime timestamp) {

}
