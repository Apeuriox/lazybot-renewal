package me.aloic.lazybot.entity;

import java.time.Instant;

public record CommandCallRecord(String userId, String channelId, Instant timestamp) {

}
