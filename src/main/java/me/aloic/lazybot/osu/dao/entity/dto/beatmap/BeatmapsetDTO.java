package me.aloic.lazybot.osu.dao.entity.dto.beatmap;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class BeatmapsetDTO implements Serializable {
    private String artist;
    private String artist_unicode;
    private Covers covers;
    private String  creator;
    private Integer favourite_count;
    private Long id;
    private Boolean nsfw;
    private Integer offset;
    private Integer play_count;
    private String preview_url;
    private String source;
    private String status;
    private Boolean spotlight;
    private String title;
    private String title_unicode;
    private Long user_id;
    private Boolean video;
//    private BeatmapDTO beatmaps;
//    private Boolean converts;
//    private NominationDTO[] current_nominations;
//    private String current_user_attributes;
//    private String description;
//    private String discussions;
//    private String events;
//    private String genre;
//    private Boolean has_favourited;
//    private String language;
//    private String nominations;
//    private String[] pack_tags;
//    private Float ratings;
//    private String recent_favourites;
//    private String related_users;
//    private String user;
//    private Long track_id;
}
