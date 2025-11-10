package me.aloic.lazybot.osu.dao.entity.dto.starmoon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScoreStarMoon implements Serializable
{
    public String id;
    public Long score;
    public Double accuracy;
    public String grade;
    public Hit hit;
    public Beatmap beatmap;
    public List<Integer> mods;
    public Date playedAt;
    public Integer maxCombo;
    public Integer rank;
    public Double pp;
    @Data
    public static class Hit {
        public Integer _50;
        public Integer _100;
        public Integer _300;
        public Integer geki;
        public Integer katu;
        public Integer miss;
    }
    @Data
    public static class Beatmap {
        public String version;
        public String md5;
        public String creator;
        public Date lastUpdate;
        public String mode;
        public Properties properties;
        public String id;
        public String foreignId;
        public Integer source;
        public Integer status;
        public Beatmapset beatmapset;
    }
    @Data
    public static class Properties {
        public Double circleSize;
        public Double approachRate;
        public Double accuracy;
        public Double hpDrain;
        public Double starRate;
        public Double bpm;
        public Integer totalLength;
        public Integer maxCombo;
        public Count count;
    }
    @Data
    public static class Count {
        public Integer circles;
        public Integer sliders;
        public Integer spinners;
    }
    @Data
    public static class Beatmapset {
        public String id;
        public Integer source;
        public Meta meta;
        public Assets assets;
        public String foreignId;
    }
    @Data
    public static class Meta {
        public Intl intl;
    }
    @Data
    public static class Intl {
        public String artist;
        public String title;
    }
    @Data
    public static class Assets {
        public String cover;
        public String cover2x;
        public String list;
        public String list2x;

        // 因为 JSON 中键名含有 "@2x"，要使用 @JsonProperty
        @com.fasterxml.jackson.annotation.JsonProperty("cover@2x")
        public void setCover2x(String v) { this.cover2x = v; }

        @com.fasterxml.jackson.annotation.JsonProperty("list@2x")
        public void setList2x(String v) { this.list2x = v; }
    }

}
