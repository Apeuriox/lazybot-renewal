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
        builder.addPath("beatmaps");
        builder.addPath(beatmapId);
        builder.addPath("scores");
        builder.addPath("users");
        builder.addPath(playerId);
        builder.addQuery("mode", mode);
        return builder.build();
    }
    public static String buildURLOfBeatmapScore(String beatmapId, String playerId,String[] modsArray,String mode)
    {
        UrlBuilder builder = UrlBuilder.ofHttp(ContentUtil.BASE_URL, CharsetUtil.CHARSET_UTF_8);
        builder.addPath("beatmaps");
        builder.addPath(beatmapId);
        builder.addPath("scores");
        builder.addPath("users");
        builder.addPath(playerId);
        for (String s : modsArray)
        {
            builder.addQuery("mods[]", s);
        }
        builder.addQuery("mode", mode);
        return builder.build();
    }
    public static String buildURLOfBeatmapScore(String beatmapId, String playerId,List<String> modsArray,String mode)
    {
        UrlBuilder builder = UrlBuilder.ofHttp(ContentUtil.BASE_URL, CharsetUtil.CHARSET_UTF_8);
        builder.addPath("beatmaps");
        builder.addPath(beatmapId);
        builder.addPath("scores");
        builder.addPath("users");
        builder.addPath(playerId);
        for (String s : modsArray)
        {
            builder.addQuery("mods[]", s);
        }
        builder.addQuery("mode", mode);
        return builder.build();
    }
    public static String buildURLOfBeatmap(String beatmapId,String mode)
    {
        UrlBuilder builder = UrlBuilder.ofHttp(ContentUtil.BASE_URL, CharsetUtil.CHARSET_UTF_8);
        builder.addPath("beatmaps");
        builder.addPath(beatmapId);
        builder.addQuery("mode", mode);
        return builder.build();
    }
    public static String buildURLOfBeatmap(String beatmapId)
    {
       return buildURLOfBeatmap(beatmapId, "osu");
    }
    public static String buildURLOfUserBest(String playerId, Integer offset,String mode)
    {
        UrlBuilder builder = UrlBuilder.ofHttp(ContentUtil.BASE_URL, CharsetUtil.CHARSET_UTF_8);
        builder.addPath("users");
        builder.addPath(playerId);
        builder.addPath("scores");
        builder.addPath("best");
        builder.addQuery("limit", 1);
        builder.addQuery("offset", offset);
        builder.addQuery("mode", mode);
        return builder.build();
    }
    public static String buildURLOfUserBest(String playerId,Integer limit, Integer offset,String mode)
    {
        UrlBuilder builder = UrlBuilder.ofHttp(ContentUtil.BASE_URL, CharsetUtil.CHARSET_UTF_8);
        builder.addPath("users");
        builder.addPath(playerId);
        builder.addPath("scores");
        builder.addPath("best");
        builder.addQuery("limit", limit);
        builder.addQuery("offset", offset);
        builder.addQuery("mode", mode);
        return builder.build();
    }
    public static String buildURLOfRecentCommand(Integer playerId, Integer type ,String mode)
    {
        return type==1? ContentUtil.BASE_URL + "/users/" + playerId + "/scores/recent?mode="+mode:
                        ContentUtil.BASE_URL + "/users/" + playerId + "/scores/recent?include_fails=1&mode="+mode;
    }
    public static String buildURLOfPlayerInfo(String playerNameOrId,String mode,String key)
    {
        UrlBuilder builder = UrlBuilder.ofHttp(ContentUtil.BASE_URL, CharsetUtil.CHARSET_UTF_8);
        playerNameOrId = Objects.equals(key, "username") ? "@".concat(playerNameOrId):playerNameOrId;
        builder.addPath("users");
        builder.addPath(playerNameOrId);
        builder.addPath(mode);
        return builder.build();
    }
    public static String buildURLOfPlayerInfo(String playerName,String mode)
    {
        UrlBuilder builder = UrlBuilder.ofHttp(ContentUtil.BASE_URL, CharsetUtil.CHARSET_UTF_8);
        builder.addPath("users");
        builder.addPath("@".concat(playerName));
        builder.addPath(mode);
        return builder.build();
    }
    public static String buildURLOfPlayerInfo(String playerName)
    {
        UrlBuilder builder = UrlBuilder.ofHttp(ContentUtil.BASE_URL, CharsetUtil.CHARSET_UTF_8);
        builder.addPath("users");
        builder.addPath("@".concat(playerName));
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
        UrlBuilder builder = UrlBuilder.ofHttp("https://osutrack-api.ameo.dev/update", CharsetUtil.CHARSET_UTF_8);
        builder.addQuery("user",playerId);
        builder.addQuery("mode", OsuMode.getMode(mode).getValue());
        return builder.build();
    }
    public static String buildURLOfOsuTrackBestPp(String mode,Integer limit)
    {
        UrlBuilder builder = UrlBuilder.ofHttp("https://osutrack-api.ameo.dev/bestplays", CharsetUtil.CHARSET_UTF_8);
        builder.addQuery("limit",limit);
        builder.addQuery("mode", OsuMode.getMode(mode).getValue());
        return builder.build();
    }
    public static String buildURLOfOsuTrackBestPp(String mode,Integer limit,String from,String to)
    {
        UrlBuilder builder = UrlBuilder.ofHttp("https://osutrack-api.ameo.dev/bestplays", CharsetUtil.CHARSET_UTF_8);
        builder.addQuery("limit",limit);
        builder.addQuery("mode", OsuMode.getMode(mode).getValue());
        builder.addQuery("from", from);
        builder.addQuery("to", to);
        return builder.build();
    }
    public static String buildURLOfBestPerformance(int playerId)
    {
        return ContentUtil.BASE_URL + "/users/" + playerId + "/scores/best?limit=100";
    }
    public static String buildURLOfOsuTrackScore(int playerId,Integer mode)
    {
        UrlBuilder builder = UrlBuilder.ofHttp(ContentUtil.OSU_TRACK_BASE_URL, CharsetUtil.CHARSET_UTF_8);
        builder.addPath("hiscores");
        builder.addQuery("user", playerId);
        builder.addQuery("mode", mode);
        return builder.build();
    }

}
