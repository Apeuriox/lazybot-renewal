package me.aloic.lazybot.osu.dao.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BeatmapSetVO {
    private String artist;
    private String creator;
    private String status;
    private Long sid;
    private String title;
}
