package me.aloic.lazybot.osu.dao.entity.dto.starmoon;

import lombok.Data;
import me.aloic.lazybot.osu.enums.OsuMod;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.enums.OsuSubruleset;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class UserResponse {
    private String id;
    private Integer stableClientId;
    private String name;
    private String safeName;
    private String flag;
    private String avatarSrc;
    private List<String> roles;
    private Object clan;
    private PreferredMode preferredMode;
    private Integer status;
    private Statistics statistics;
    private Object relationships;
    private Object email;
    private Profile profile;

    @Data
    public static class PreferredMode {
        private String mode;
        private String ruleset;
    }

    @Data
    public static class Profile {
        private String html;
        private Raw raw;

        @Data
        public static class Raw {}
    }

    @Data
    public static class Statistics {
        private ModeStatistics osu;
        private ModeStatistics taiko;
        private ModeStatistics fruits;
        private ModeStatistics mania;

        @Data
        public static class ModeStatistics {
            private RulesetStatistics standard;
            private RulesetStatistics relax;
            private RulesetStatistics autopilot;

            @Data
            public static class RulesetStatistics {
                private RankPerformance ppv2;
                private RankPerformance rankedScore;
                private RankPerformance totalScore;
                private Long playCount;
                private Long playTime;
                private Long totalHits;
                private Double level;
                private Long maxCombo;
                private Long replayWatchedByOthers;
                private ScoreRankComposition scoreRankComposition;

                @Data
                public static class RankPerformance {
                    private Integer rank;
                    private Integer countryRank;
                    private Double performance;
                    private String score; // “xxn” 转为字符串保存
                }

                @Data
                public static class ScoreRankComposition {
                    private Integer ssh;
                    private Integer ss;
                    private Integer sh;
                    private Integer s;
                    private Integer a;
                    private Integer b;
                    private Integer c;
                    private Integer d;
                    private Integer f;
                }
            }
        }
    }

    public Statistics.ModeStatistics.RulesetStatistics getTargetRulesetStatistics(OsuMode mode, OsuSubruleset ruleset)
    {
        return switch (mode){
            case Osu -> getTargetRulesetStatistics(this.getStatistics().getOsu(),ruleset);
            case Taiko -> getTargetRulesetStatistics(this.getStatistics().getTaiko(),ruleset);
            case Catch -> getTargetRulesetStatistics(this.getStatistics().getFruits(),ruleset);
            case Mania -> getTargetRulesetStatistics(this.getStatistics().getMania(),ruleset);
            case Default -> null;
        };

    }
    public Statistics.ModeStatistics.RulesetStatistics getTargetRulesetStatistics(Statistics.ModeStatistics stats, OsuSubruleset ruleset)
    {
        return switch (ruleset){
            case Standard -> stats.getStandard();
            case Relax -> stats.getRelax();
            case Autopilot -> stats.getAutopilot();
            case Default -> null;
        };
    }
}
