package me.aloic.lazybot.util;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.CharsetUtil;
import me.aloic.lazybot.osu.enums.OsuMode;
import java.util.List;
import java.util.Objects;

public class URLBuildUtil
{
    public static String buildURLOfBeatmapScore(String beatmapId, String playerId, String mode)
    {
        UrlBuilder builder = UrlBuilder.ofHttp(ContentUtil.BASE_URL, CharsetUtil.CHARSET_UTF_8);
        builder.addPath("beatmaps")
                .addPath(beatmapId)
                .addPath("scores")
                .addPath("users")
                .addPath(playerId)
                .addQuery("mode", mode);
        return builder.build();
    }
    public static String buildURLOfBeatmapScoreAll(String beatmapId, String playerId, String mode)
    {
        UrlBuilder builder = UrlBuilder.ofHttp(ContentUtil.BASE_URL, CharsetUtil.CHARSET_UTF_8);
        builder.addPath("beatmaps")
                .addPath(beatmapId)
                .addPath("scores")
                .addPath("users")
                .addPath(playerId)
                .addPath("all")
                .addQuery("ruleset", mode);
        return builder.build();
    }
    public static String buildURLOfBeatmapScore(String beatmapId, String playerId,String[] modsArray,String mode)
    {
        UrlBuilder builder = UrlBuilder.ofHttp(ContentUtil.BASE_URL, CharsetUtil.CHARSET_UTF_8);
        builder.addPath("beatmaps")
                .addPath(beatmapId)
                .addPath("scores")
                .addPath("users")
                .addPath(playerId);
        for (String s : modsArray)
        {
            builder.addQuery("mods[]", s);
        }
        builder.addQuery("mode", mode);
        return builder.build();
    }
    public static String buildURLOfBeatmapScore(String beatmapId, String playerId,List<String> modsArray,String mode)
    {
        UrlBuilder builder = UrlBuilder.ofHttp(ContentUtil.BASE_URL, CharsetUtil.CHARSET_UTF_8)
                .addPath("beatmaps")
                .addPath(beatmapId)
                .addPath("scores")
                .addPath("users")
                .addPath(playerId);
        for (String s : modsArray)
        {
            builder.addQuery("mods[]", s);
        }
        builder.addQuery("mode", mode);
        return builder.build();
    }
    public static String buildURLOfBeatmap(String beatmapId,String mode)
    {
        UrlBuilder builder = UrlBuilder.ofHttp(ContentUtil.BASE_URL, CharsetUtil.CHARSET_UTF_8)
                .addPath("beatmaps")
                .addPath(beatmapId)
                .addQuery("mode", mode);
        return builder.build();
    }
    public static String buildURLOfBeatmap(String beatmapId)
    {
       return buildURLOfBeatmap(beatmapId, "osu");
    }
    public static String buildURLOfUserBest(String playerId, Integer offset,String mode)
    {
        UrlBuilder builder = UrlBuilder.ofHttp(ContentUtil.BASE_URL, CharsetUtil.CHARSET_UTF_8)
                .addPath("users")
                .addPath(playerId)
                .addPath("scores")
                .addPath("best")
                .addQuery("limit", 1)
                .addQuery("offset", offset)
                .addQuery("mode", mode);
        return builder.build();
    }
    public static String buildURLOfUserBest(String playerId,Integer limit, Integer offset,String mode)
    {
        UrlBuilder builder = UrlBuilder.ofHttp(ContentUtil.BASE_URL, CharsetUtil.CHARSET_UTF_8)
                .addPath("users")
                .addPath(playerId)
                .addPath("scores")
                .addPath("best")
                .addQuery("limit", limit)
                .addQuery("offset", offset)
                .addQuery("mode", mode);
        return builder.build();
    }
    public static String buildURLOfRecentCommand(Integer playerId, Integer type, Integer limit ,String mode)
    {
        UrlBuilder builder = UrlBuilder.ofHttp(ContentUtil.BASE_URL, CharsetUtil.CHARSET_UTF_8)
                .addPath("users")
                .addPath(String.valueOf(playerId))
                .addPath("scores")
                .addPath("recent")
                .addQuery("mode", mode)
                .addQuery("limit", limit);
                if(type!=1)
                     builder.addQuery("include_fails", 1);
        return builder.build();
    }
    public static String buildURLOfPlayerInfo(String playerNameOrId,String mode,String key)
    {
        UrlBuilder builder = UrlBuilder.ofHttp(ContentUtil.BASE_URL, CharsetUtil.CHARSET_UTF_8);
        playerNameOrId = Objects.equals(key, "username") ? "@".concat(playerNameOrId):playerNameOrId;
        builder.addPath("users")
                .addPath(playerNameOrId)
                .addPath(mode);
        return builder.build();
    }
    public static String buildURLOfPlayerInfo(String playerName,String mode)
    {
        UrlBuilder builder = UrlBuilder.ofHttp(ContentUtil.BASE_URL, CharsetUtil.CHARSET_UTF_8)
                .addPath("users")
                .addPath("@".concat(playerName))
                .addPath(mode);
        return builder.build();
    }
    public static String buildURLOfPlayerInfo(Integer playerId,String mode)
    {
        UrlBuilder builder = UrlBuilder.ofHttp(ContentUtil.BASE_URL, CharsetUtil.CHARSET_UTF_8)
                .addPath("users")
                .addPath(String.valueOf(playerId))
                .addPath(mode);
        return builder.build();
    }
    public static String buildURLOfPlayerInfo(String playerName)
    {
        UrlBuilder builder = UrlBuilder.ofHttp(ContentUtil.BASE_URL, CharsetUtil.CHARSET_UTF_8)
                .addPath("users")
                .addPath("@".concat(playerName));
        return builder.build();
    }
    public static String buildURLOfPlayerInfoArray(List<String> playerIds, String mode)
    {
        UrlBuilder builder = UrlBuilder.ofHttp("https://osu.ppy.sh/api/v2/users", CharsetUtil.CHARSET_UTF_8);
        for(String id:playerIds)
        {
            builder.addQuery("ids[]",id);
        }
        return builder.build();
    }
    public static String buildURLOfOsuTrackUpdate(Integer playerId, String mode)
    {
        UrlBuilder builder = UrlBuilder.ofHttp("https://osutrack-api.ameo.dev/update", CharsetUtil.CHARSET_UTF_8)
                .addQuery("user",playerId)
                .addQuery("mode", OsuMode.getMode(mode).getValue());
        return builder.build();
    }
    public static String buildURLOfOsuTrackBestPp(String mode,Integer limit)
    {
        UrlBuilder builder = UrlBuilder.ofHttp("https://osutrack-api.ameo.dev/bestplays", CharsetUtil.CHARSET_UTF_8)
                .addQuery("limit",limit)
                .addQuery("mode", OsuMode.getMode(mode).getValue());
        return builder.build();
    }
    public static String buildURLOfOsuTrackBestPp(String mode,Integer limit,String from,String to)
    {
        UrlBuilder builder = UrlBuilder.ofHttp("https://osutrack-api.ameo.dev/bestplays", CharsetUtil.CHARSET_UTF_8)
                .addQuery("limit",limit)
                .addQuery("mode", OsuMode.getMode(mode).getValue())
                .addQuery("from", from)
                .addQuery("to", to);
        return builder.build();
    }
    public static String buildURLOfBestPerformance(int playerId)
    {
        return ContentUtil.BASE_URL + "/users/" + playerId + "/scores/best?limit=100";
    }
    public static String buildURLOfOsuTrackScore(int playerId,Integer mode)
    {
        UrlBuilder builder = UrlBuilder.ofHttp(ContentUtil.OSU_TRACK_BASE_URL, CharsetUtil.CHARSET_UTF_8)
                .addPath("hiscores")
                .addQuery("user", playerId)
                .addQuery("mode", mode);
        return builder.build();
    }
    public static String buildURLOfOsuTrackBestPlays(Integer limit,Integer mode)
    {
        UrlBuilder builder = UrlBuilder.ofHttp(ContentUtil.OSU_TRACK_BASE_URL, CharsetUtil.CHARSET_UTF_8)
                .addPath("bestplays")
                .addQuery("limit", limit)
                .addQuery("mode", mode);
        return builder.build();
    }
    public static String buildURLOfOsuTrackBestPlays(Integer limit,Integer mode,String from,String to)
    {
        UrlBuilder builder = UrlBuilder.ofHttp(ContentUtil.OSU_TRACK_BASE_URL, CharsetUtil.CHARSET_UTF_8)
                .addPath("bestplays")
                .addQuery("limit", limit)
                .addQuery("mode", mode)
                .addQuery("from", from)
                .addQuery("to", to);
        return builder.build();
    }
    public static String buildURLOfPpRank(Integer mode,Integer pp)
    {
        UrlBuilder builder = UrlBuilder.ofHttp("https://osudaily.net/data/getPPRank.php", CharsetUtil.CHARSET_UTF_8)
                .addQuery("m", 0)
                .addQuery("t","pp")
                .addQuery("v", pp);
        return builder.build();
    }
    public static String buildURLOfPlayerPerformancePlus(Integer id)
    {
        UrlBuilder builder = UrlBuilder.ofHttp("https://114.242.29.30:41000", CharsetUtil.CHARSET_UTF_8)
                .addPath("lazybot")
                .addPath("player")
                .addPath("info")
                .addQuery("id", id);
        return builder.build();
    }

}
