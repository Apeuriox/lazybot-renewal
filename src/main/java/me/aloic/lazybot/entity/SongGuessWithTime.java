package me.aloic.lazybot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.BeatmapDTO;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class SongGuessWithTime
{
    private final String title;
    private final String mapper;
    private final String artist;
    private final Integer bid;


    private final LocalDateTime startTime;

    public SongGuessWithTime(String title, String mapper, String artist, Integer bid) {
        this.title = title;
        this.mapper = mapper;
        this.artist = artist;
        this.startTime = LocalDateTime.now();
        this.bid = bid;
    }

}