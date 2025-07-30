package me.aloic.lazybot.osu.dao.entity.dto.lazybot;

import lombok.Data;
import me.aloic.lazybot.entity.SongGuessWithTime;

@Data
public class LazybotSongGuessData
{
    private SongGuessWithTime meta;
    private byte[] img;
    private int resizeLevel;
}
