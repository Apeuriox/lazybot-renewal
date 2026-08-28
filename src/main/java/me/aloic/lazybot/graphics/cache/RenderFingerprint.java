package me.aloic.lazybot.graphics.cache;

import me.aloic.lazybot.entity.command.AddScorePlus;
import me.aloic.lazybot.entity.command.ComparePlayerBpList;
import me.aloic.lazybot.entity.command.MoelleuxCard;
import me.aloic.lazybot.entity.command.PerformancePlusProfile;
import me.aloic.lazybot.entity.command.PlayerScoreList;
import me.aloic.lazybot.entity.command.ProfileInfo;
import me.aloic.lazybot.entity.command.UserAllScore;
import me.aloic.lazybot.entity.po.BadgeUserShowcasePO;
import me.aloic.lazybot.entity.vo.ThumbnailClassicalVO;
import me.aloic.lazybot.graphics.mapping.documentMapper.ThumbnailSVGMapper;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.dto.player.TeamDTO;
import me.aloic.lazybot.osu.dao.entity.dto.plus.LazybotBeatmap;
import me.aloic.lazybot.osu.dao.entity.dto.plus.LazybotScorePerformance;
import me.aloic.lazybot.osu.dao.entity.dto.plus.LazybotScoreStatistics;
import me.aloic.lazybot.osu.dao.entity.dto.plus.ScorePerformanceDTO;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.Mod;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.ModSetting;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.ScoreStatisticsLazer;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.player.GradeCounts;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.player.Level;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.player.Statistics;
import me.aloic.lazybot.osu.dao.entity.po.CommandUsage;
import me.aloic.lazybot.osu.dao.entity.po.LazybotUsageCommand;
import me.aloic.lazybot.osu.dao.entity.po.LazybotUsageSource;
import me.aloic.lazybot.osu.dao.entity.po.LazybotUsageTimeDistribution;
import me.aloic.lazybot.osu.dao.entity.vo.BeatmapAttributeVO;
import me.aloic.lazybot.osu.dao.entity.vo.BeatmapPerformance;
import me.aloic.lazybot.osu.dao.entity.vo.BeatmapStatistics;
import me.aloic.lazybot.osu.dao.entity.vo.BeatmapVO;
import me.aloic.lazybot.osu.dao.entity.vo.ImaginaryPerformance;
import me.aloic.lazybot.osu.dao.entity.vo.MapPerformanceAnalysis;
import me.aloic.lazybot.osu.dao.entity.vo.MapScore;
import me.aloic.lazybot.osu.dao.entity.vo.NoChokeListVO;
import me.aloic.lazybot.osu.dao.entity.vo.PPPlusPerformance;
import me.aloic.lazybot.osu.dao.entity.vo.PPPlusScore;
import me.aloic.lazybot.osu.dao.entity.vo.PerformanceVO;
import me.aloic.lazybot.osu.dao.entity.vo.PlayerInfoMoelleux;
import me.aloic.lazybot.osu.dao.entity.vo.PlayerInfoVO;
import me.aloic.lazybot.osu.dao.entity.vo.PlusScorePerformance;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreSequence;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import me.aloic.lazybot.osu.theme.Color.HSL;
import me.aloic.lazybot.osu.theme.preset.ProfileTheme;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class RenderFingerprint
{
    public static final String SCHEMA = "1";

    private final String rendererName;
    private final MessageDigest digest;

    private RenderFingerprint(String rendererName)
    {
        this.rendererName = rendererName;
        try {
            this.digest = MessageDigest.getInstance("SHA-256");
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("[Cache Fingerprint] SHA-256 unavailable: ", e);
        }
        add("schema", SCHEMA);
        add("rendererName", rendererName);
    }

    public static RenderFingerprint of(String renderer)
    {
        return new RenderFingerprint(renderer);
    }

    public String key()
    {
        return rendererName + ":" + SCHEMA + ":" + HexFormat.of().formatHex(digest.digest());
    }

    public RenderFingerprint add(String name, Object value)
    {
        write(name);
        digest.update((byte) 0);
        write(value == null ? "null" : String.valueOf(value));
        digest.update((byte) 1);
        return this;
    }
    // we dont need every attr being passed into fingerprint
    public RenderFingerprint addScore(ScoreVO score)
    {
        if (score == null)
            return add("score", null);
        add("user_name", score.getUser_name());
        add("pp", score.getPp());
        add("mode", score.getMode());
        addPerformance(score.getPpDetailsLocal());
        add("osuSubServer", score.getOsuSubServer() == null ? null : score.getOsuSubServer().name());
        if (score instanceof PPPlusScore plus) {
            addPlus(plus.getPlusPerformance());
            addPlus(plus.getMaxPerformance());
        }
        return this;
    }

    public RenderFingerprint addScoreList(List<ScoreVO> scores)
    {
        if (scores == null)
            return add("scores", null);
        add("scores.size", scores.size());
        for (ScoreVO score : scores) {
            addScore(score);
        }

        return this;
    }

    public RenderFingerprint addScoreSequence(ScoreSequence sequence)
    {
        if (sequence == null)
            return add("sequence", null);
        add("playerName", sequence.getPlayerName());
        add("accuracy", sequence.getAccuracy());
        addMods(sequence.getModList());
        add("achievedTime", sequence.getAchievedTime());
        add("rulesetId", sequence.getRulesetId());
        if (sequence instanceof MapScore mapScore) {
            add("starRating", mapScore.getStarRating());
        }
        return this;
    }

    public RenderFingerprint addScoreSequences(List<ScoreSequence> sequences)
    {
        if (sequences == null)
            return add("sequences", null);
        add("sequences.size", sequences.size());
        for (ScoreSequence sequence : sequences)
            addScoreSequence(sequence);
        return this;
    }

    public RenderFingerprint addPlayer(PlayerInfoVO info)
    {
        if (info == null)
            return add("player", null);
        add("id", info.getId());
        add("playerName", info.getPlayerName());
        add("mode", info.getMode());
        add("performancePoint", info.getPerformancePoint());
        add("rankTotalScore", info.getRankTotalScore());
        add("playCount", info.getPlayCount());
        add("totalHitCount", info.getTotalHitCount());
        add("totalPlayTime", info.getTotalPlayTime());
        add("primaryColor", info.getPrimaryColor());
        addGrades(info.getGrades());
        add("totalScore", info.getTotalScore());
        add("playStyles", info.getPlayStyles());
        return this;
    }

    public RenderFingerprint addBeatmap(BeatmapVO beatmap)
    {
        if (beatmap == null)
            return add("beatmap", null);
        add("accuracy", beatmap.getAccuracy());
        add("beatmapset_id", beatmap.getBeatmapset_id());
        add("mode_int", beatmap.getMode_int());
        add("difficult_rating", beatmap.getDifficult_rating());
        add("status", beatmap.getStatus());
        add("sid", beatmap.getSid());
        add("bid", beatmap.getBid());
        addAttributes(beatmap.getAttributes());
        add("checksum", beatmap.getChecksum());
        if (beatmap instanceof BeatmapPerformance performance) {
            add("lengthBonus", performance.getLengthBonus());
            add("playCount", performance.getPlayCount());
            add("mode", performance.getMode());
        }
        return this;
    }

    public RenderFingerprint addPlayerScoreList(PlayerScoreList list)
    {
        if (list == null)
            return add("playerScoreList", null);
        addPlayer(list.getInfo());
        addScoreList(list.getScoreVOList());
        addScoreSequences(list.getScoreSequences());
        return this;
    }

    public RenderFingerprint addNoChoke(NoChokeListVO list)
    {
        if (list == null)
            return add("noChoke", null);
        addPlayer(list.getInfo());
        addScoreList(list.getScoreList());
        return this;
    }

    public RenderFingerprint addMoelleux(MoelleuxCard card)
    {
        if (card == null)
            return add("moelleux", null);
        addMoelleuxInfo(card.getInfo());
        add("primaryHue", card.getPrimaryHue());
        add("isLowSaturation", card.getIsLowSaturation());
        add("enableWhiteMask", card.getEnableWhiteMask());
        return this;
    }

    public RenderFingerprint addBeatmapStatistics(BeatmapStatistics stats)
    {
        if (stats == null)
            return add("beatmapStatistics", null);
        addBeatmap(stats.getBeatmap());
        add("mode", stats.getMode());
        addImaginary(stats.getPerformance());
        addMods(stats.getImaginaryMods());
        add("ppBreakdownRatioChain", stats.getPpBreakdownRatioChain());
        return this;
    }

    public RenderFingerprint addThumbnail(ThumbnailClassicalVO data)
    {
        if (data == null)
            return add("thumbnail", null);
        addPlayer(data.getPlayer());
        addScore(data.getScore());
        add("comment", data.getComment());
        add("position", data.getPosition());
        if (data.getAttributes() == null)
            add("attributes", null);
        else
        {
            add("attributes.size", data.getAttributes().size());
            for (ThumbnailSVGMapper.ThumbnailClassicalAttribute attribute : data.getAttributes())
                add("attribute", attribute == null ? null : attribute.name());
        }
        return this;
    }

    public RenderFingerprint addCompare(ComparePlayerBpList data)
    {
        if (data == null)
            return add("compare", null);
        addPlayerDto(data.getInfo());
        addPlayerDto(data.getCompareInfo());
        addScoreLazerList(data.getScoreList());
        addScoreLazerList(data.getCompareScoreList());
        return this;
    }

    public RenderFingerprint addPlusProfile(PerformancePlusProfile profile)
    {
        if (profile == null)
            return add("plusProfile", null);
        addPlus(profile.getPerformance());
        addPlayer(profile.getPlayer());
        return this;
    }

    public RenderFingerprint addPlusList(PlusScorePerformance performance)
    {
        if (performance == null)
            return add("plusList", null);
        add("name", performance.getName());
        add("offset", performance.getOffset());
        add("dimension", performance.getDimension());
        addScore(performance);
        if (performance.getScores() == null)
            add("plusScores", null);
        else
        {
            add("plusScores.size", performance.getScores().size());
            for (ScorePerformanceDTO score : performance.getScores())
                addScorePerformance(score);
        }
        return this;
    }

    public RenderFingerprint addMapScore(UserAllScore data)
    {
        if (data == null)
            return add("mapScore", null);
        addBeatmap(data.getBeatmapPerformance());
        if (data.getMapScoreList() == null)
            add("mapScores", null);
        else
        {
            add("mapScores.size", data.getMapScoreList().size());
            for (MapScore score : data.getMapScoreList())
                addScoreSequence(score);
        }
        return this;
    }

    public RenderFingerprint addAddScore(AddScorePlus score)
    {
        if (score == null)
            return add("addScore", null);
        addScore(score.getScore());
        addLazybotPerformance(score.getScorePlus());
        return this;
    }

    public RenderFingerprint addProfile(ProfileInfo info)
    {
        if (info == null)
            return add("profile", null);
        addPlayer(info.getInfo());
        addTheme(info.getTheme());
        if (info.getBadges() == null)
            add("badges", null);
        else
        {
            add("badges.size", info.getBadges().size());
            for (BadgeUserShowcasePO badge : info.getBadges())
            {
                if (badge == null)
                    add("badge", null);
                else
                {
                    add("badge.id", badge.getId());
                    add("badge.badge_id", badge.getBadge_id());
                    add("badge.lazybot_id", badge.getLazybot_id());
                }
            }
        }
        return this;
    }

    public RenderFingerprint addUsage(CommandUsage usage)
    {
        if (usage == null)
            return add("usage", null);
        add("id", usage.getId());
        add("total", usage.getTotal());
        if (usage.getCommand() == null)
            add("command", null);
        else
        {
            add("command.size", usage.getCommand().size());
            for (LazybotUsageCommand item : usage.getCommand())
            {
                add("command.name", item == null ? null : item.getCommand());
                add("command.count", item == null ? null : item.getCount());
            }
        }
        return this;
    }

    public RenderFingerprint addMapAnalysis(MapPerformanceAnalysis analysis)
    {
        if (analysis == null)
            return add("mapAnalysis", null);
        addBeatmapStatistics(analysis.context());
        add("targetAccuracy", analysis.targetAccuracy());
        add("starRating", analysis.starRating());
        add("history.size", analysis.history().size());
        return this;
    }


    private void addMoelleuxInfo(PlayerInfoMoelleux info)
    {
        if (info == null)
        {
            add("moelleuxInfo", null);
            return;
        }
        addPlayer(info.getInfo());
        addScoreList(info.getBps());
        addPlus(info.getPlus());
    }

    private void addMods(List<Mod> mods)
    {
        if (mods == null) {
            add("mods", null);
            return;
        }
        add("mods.size", mods.size());
        for (Mod mod : mods) {
            if (mod == null) {
                add("mod", null);
                continue;
            }
            add("mod.acronym", mod.getAcronym());
        }
    }

    private void addStatistics(ScoreStatisticsLazer statistics)
    {
        if (statistics == null)
        {
            add("statistics", null);
            return;
        }
        add("ok", statistics.getOk());
        add("meh", statistics.getMeh());
        add("miss", statistics.getMiss());
        add("good", statistics.getGood());
        add("great", statistics.getGreat());
        add("perfect", statistics.getPerfect());
        add("ignore_hit", statistics.getIgnore_hit());
        add("ignore_miss", statistics.getIgnore_miss());
        add("small_bonus", statistics.getSmall_bonus());
        add("large_tick_hit", statistics.getLarge_tick_hit());
        add("slider_tail_hit", statistics.getSlider_tail_hit());
        add("small_tick_miss", statistics.getSmall_tick_miss());
        add("small_tick_hit", statistics.getSmall_tick_hit());
        add("large_bonus", statistics.getLarge_bonus());
    }

    private void addPerformance(PerformanceVO performance)
    {
        if (performance == null)
        {
            add("performance", null);
            return;
        }
        add("ifFc", performance.getIfFc());
        add("currentPP", performance.getCurrentPP());
        add("star", performance.getStar());
        add("taikoDifficulty", performance.getTaikoDifficulty());
    }

    private void addPlus(PPPlusPerformance plus)
    {
        if (plus == null)
        {
            add("plus", null);
            return;
        }
        add("plus.pp", plus.getPp());
        add("plus.ppAim", plus.getPpAim());
        add("plus.ppJumpAim", plus.getPpJumpAim());
        add("plus.ppFlowAim", plus.getPpFlowAim());
        add("plus.ppPrecision", plus.getPpPrecision());
        add("plus.ppSpeed", plus.getPpSpeed());
        add("plus.ppStamina", plus.getPpStamina());
        add("plus.ppAcc", plus.getPpAcc());
        add("plus.effectiveMissCount", plus.getEffectiveMissCount());
        add("plus.iffc", plus.getIffc());
    }

    private void addImaginary(ImaginaryPerformance performance)
    {
        if (performance == null)
        {
            add("imaginary", null);
            return;
        }
        addPpMap("imaginary.accPPList", performance.getAccPPList());
        add("imaginary.aimPP", performance.getAimPP());
        add("imaginary.spdPP", performance.getSpdPP());
        add("imaginary.accPP", performance.getAccPP());
        add("imaginary.readPP", performance.getReadPP());
        add("imaginary.imaginaryAccuracy", performance.getImaginaryAccuracy());
        add("imaginary.flashlightPP", performance.getFlashlightPP());
        add("imaginary.star", performance.getStar());
        add("imaginary.imaginaryPP", performance.getImaginaryPP());
    }

    private void addAttributes(BeatmapAttributeVO attributes)
    {
        if (attributes == null)
        {
            add("attributes", null);
            return;
        }
        add("attr.ar", attributes.getAr());
        add("attr.od", attributes.getOd());
        add("attr.cs", attributes.getCs());
        add("attr.hp", attributes.getHp());
        add("attr.bpm", attributes.getBpm());
        add("attr.mode", attributes.getMode());
        add("attr.length", attributes.getLength());
    }

    private void addGrades(GradeCounts grades)
    {
        if (grades == null)
        {
            add("grades", null);
            return;
        }
        add("grades.ss", grades.getSs());
        add("grades.ssh", grades.getSsh());
        add("grades.s", grades.getS());
        add("grades.sh", grades.getSh());
        add("grades.a", grades.getA());
    }

    private void addPlayerDto(PlayerInfoDTO info)
    {
        if (info == null)
        {
            add("playerDto", null);
            return;
        }
        add("dto.id", info.getId());
        add("dto.username", info.getUsername());
        add("dto.country_code", info.getCountry_code());
        add("dto.playmode", info.getPlaymode());
        add("dto.join_date", info.getJoin_date());
        add("dto.profile_hue", info.getProfile_hue());
        add("dto.is_supporter", info.getIs_supporter());
        add("dto.support_level", info.getSupport_level());
        TeamDTO team = info.getTeam();
        if (team == null)
            add("dto.team", null);
        else
        {
            add("dto.team.id", team.getId());
            add("dto.team.name", team.getName());
            add("dto.team.short_name", team.getShort_name());
        }
        addStatisticsDto(info.getStatistics());
    }

    private void addStatisticsDto(Statistics statistics)
    {
        if (statistics == null)
        {
            add("dto.statistics", null);
            return;
        }
        add("dto.pp", statistics.getPp());
        add("dto.global_rank", statistics.getGlobal_rank());
        add("dto.country_rank", statistics.getCountry_rank());
        add("dto.ranked_score", statistics.getRanked_score());
        add("dto.hit_accuracy", statistics.getHit_accuracy());
        add("dto.play_count", statistics.getPlay_count());
        add("dto.play_time", statistics.getPlay_time());
        add("dto.total_score", statistics.getTotal_score());
        add("dto.total_hits", statistics.getTotal_hits());
        add("dto.is_ranked", statistics.getIs_ranked());
        addGrades(statistics.getGrade_counts());
        Level level = statistics.getLevel();
        if (level == null)
            add("dto.level", null);
        else
        {
            add("dto.level.current", level.getCurrent());
            add("dto.level.progress", level.getProgress());
        }
    }

    private void addScoreLazerList(List<ScoreLazerDTO> scores)
    {
        if (scores == null)
        {
            add("scoreLazerList", null);
            return;
        }
        add("scoreLazerList.size", scores.size());
        for (ScoreLazerDTO score : scores)
        {
            if (score == null)
            {
                add("scoreLazer", null);
                continue;
            }
            add("lazer.id", score.getId());
            add("lazer.beatmap_id", score.getBeatmap_id());
            add("lazer.user_id", score.getUser_id());
            add("lazer.pp", score.getPp());
            add("lazer.ended_at", score.getEnded_at());
        }
    }

    private void addScorePerformance(ScorePerformanceDTO score)
    {
        if (score == null)
        {
            add("scorePerformance", null);
            return;
        }
        add("sp.scoreId", score.getScoreId());
        add("sp.pp", score.getPp());
    }

    private void addLazybotPerformance(LazybotScorePerformance performance)
    {
        if (performance == null)
        {
            add("lazybotPerformance", null);
            return;
        }
        add("lp.scoreId", performance.getScoreId());
        add("lp.pp", performance.getPp());
        add("lp.createdAt", performance.getCreatedAt());
    }

    private void addLazybotBeatmap(LazybotBeatmap beatmap)
    {
        if (beatmap == null)
        {
            add("lazybotBeatmap", null);
            return;
        }
        add("lb.id", beatmap.getId());
        add("lb.checksum", beatmap.getChecksum());
        add("lb.sid", beatmap.getSid());
        add("lb.star", beatmap.getStar());
    }

    private void addLazybotStatistics(LazybotScoreStatistics statistics)
    {
        if (statistics == null)
        {
            add("lazybotStatistics", null);
            return;
        }
        add("ls.scoreId", statistics.getScoreId());
        add("ls.count300", statistics.getCount300());
        add("ls.count100", statistics.getCount100());
        add("ls.count50", statistics.getCount50());
        add("ls.count0", statistics.getCount0());
        add("ls.countTick", statistics.getCountTick());
        add("ls.countEnd", statistics.getCountEnd());
    }

    private void addTheme(ProfileTheme theme)
    {
        if (theme == null)
        {
            add("theme", null);
            return;
        }
        add("theme.type", theme.getThemeType());
        add("theme.hue", theme.getHue());
        addHsl("main", theme.getMainColor());
        addHsl("mainMiddle", theme.getMainMiddleColor());
        addHsl("lightFont", theme.getLightFontColor());
        addHsl("header", theme.getHeaderColor());
        addHsl("border", theme.getBorderColor());
        addHsl("headerBorder", theme.getHeaderBorderColor());
        addHsl("block", theme.getBlockColor());
        addHsl("blockLighter", theme.getBlockColorLighter());
        addHsl("levelProgressBg", theme.getLevelProgressBackgroundColor());
        addHsl("modeInactive", theme.getModeInactiveColor());
    }

    private void addHsl(String name, HSL hsl)
    {
        if (hsl == null)
        {
            add("hsl." + name, null);
            return;
        }
        add("hsl." + name + ".h", hsl.getHue());
        add("hsl." + name + ".s", hsl.getSaturation());
        add("hsl." + name + ".l", hsl.getLightness());
    }

    private void addPpMap(String name, Map<Integer, Double> values)
    {
        if (values == null)
        {
            add(name, null);
            return;
        }
        add(name + ".size", values.size());
        for (Map.Entry<Integer, Double> entry : new TreeMap<>(values).entrySet())
        {
            add(name + ".k", entry.getKey());
            add(name + ".v", entry.getValue());
        }
    }

    private void write(String value)
    {
        digest.update(value.getBytes(StandardCharsets.UTF_8));
    }
}
