package me.aloic.lazybot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class GameWithTime {
    private final String original;
    private final LocalDateTime startTime;
    private String masked;

    public GameWithTime(String original, LocalDateTime startTime) {
        this.original = original;
        this.startTime = startTime;
    }

}