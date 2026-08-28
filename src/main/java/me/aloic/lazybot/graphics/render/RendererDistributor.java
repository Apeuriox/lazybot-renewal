package me.aloic.lazybot.graphics.render;

import me.aloic.lazybot.entity.command.*;
import me.aloic.lazybot.entity.vo.ThumbnailClassicalVO;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.graphics.cache.BitmapRenderCache;
import me.aloic.lazybot.graphics.cache.RenderFingerprint;
import me.aloic.lazybot.graphics.mapping.documentMapper.*;
import me.aloic.lazybot.osu.dao.entity.po.CommandUsage;
import me.aloic.lazybot.osu.dao.entity.vo.*;
import me.aloic.lazybot.util.CommonTool;
import me.aloic.lazybot.util.TransformerUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class RendererDistributor
{
    private static volatile BitmapRenderCache renderCache;

    public static void bindCache(BitmapRenderCache cache)
    {
        renderCache = cache;
    }

    public static byte[] renderScoreVOToImage(ScoreVO score, int version) throws IOException
    {
        return cached(RenderFingerprint.of("score").add("version", version).addScore(score),
                () -> SVGRenderer.renderSVGDocumentToByteArray(ScoreSVGMapper.renderScoreToImage(score,
                        version,
                        CommonTool.getDominantColorArray(score)),
                        Math.max(1, version - 725)
                )
        );
    }

    public static byte[] renderBeatmapStatisticsToImage(BeatmapStatistics bs) throws IOException
    {
        return cached(RenderFingerprint.of("beatmap-stats").addBeatmapStatistics(bs),
                () -> SVGRenderer.renderSVGDocumentToByteArray(
                        MapSVGMapper.mapBeatmapStatsToPanel(bs)
                )
        );
    }


    public static byte[] renderPPPlusScoreToQuadraGrid(PPPlusScore scorePlus) throws IOException
    {
        return cached(RenderFingerprint.of("ppplus-score").addScore(scorePlus),
                () -> SVGRenderer.renderSVGDocumentToByteArray(
                        PlusScoreSVGMapper.mapPlusScoreToQuadraGrid(scorePlus,
                                CommonTool.getDominantHueColorThief(new File(scorePlus.getBeatmap().getBgUrl()))
                        )
                )
        );
    }

    public static byte[] renderPlusScoresToCardList(PlusScorePerformance performance)
    {
        return cached(RenderFingerprint.of("ppplus-score-list").addPlusList(performance),
                () -> SVGRenderer.renderSVGDocumentToByteArray(ScoreListSVGMapper.mapScorePerformanceDimensionToBpCard(performance)
                )
        );
    }

    public static byte[] renderThumbnailClassical(ThumbnailClassicalVO tcData)
    {
        return cached(RenderFingerprint.of("thumbnail-classical").addThumbnail(tcData),
                () -> SVGRenderer.renderSVGDocumentToByteArray(ThumbnailSVGMapper.mapToThumbnailClassical(tcData))
        );
    }


    public static byte[] renderMapScore(UserAllScore uas, Boolean ignoreBanner)
    {
        return cached(RenderFingerprint.of("map-score").addMapScore(uas),
                () -> SVGRenderer.renderSVGDocumentToByteArray(
                        MapScoreSVGMapper.mapMapScoreListToAllScorePanel(uas.getMapScoreList(), uas.getBeatmapPerformance(), false),
                        2f)
        );
    }
    public static byte[] renderPlayerScoreListToCard(PlayerScoreList player, int offset, int type)
    {
        return cached(RenderFingerprint.of("bp-card")
                        .add("offset", offset)
                        .add("type", type)
                        .addPlayerScoreList(player),
                () -> SVGRenderer.renderSVGDocumentToByteArray(
                        ScoreListSVGMapper.mapScoreListToBpCard(player.getInfo(), player.getScoreVOList(), offset, type)
                )
        );
    }
    public static byte[] renderPlayerScoreListToCard(NoChokeListVO player, int offset, int type)
    {
        return cached(RenderFingerprint.of("bp-card-nochoke")
                        .add("offset", offset)
                        .add("type", type)
                        .addNoChoke(player),
                () -> SVGRenderer.renderSVGDocumentToByteArray(
                        ScoreListSVGMapper.mapScoreListToBpCard(player.getInfo(), player.getScoreList(), offset, type)
                )
        );
    }
    public static byte[] renderPlayerScoreListToList(PlayerScoreList player, int offset)
    {
        return cached(RenderFingerprint.of("bp-list")
                        .add("offset", offset)
                        .addPlayerScoreList(player),
                () -> SVGRenderer.renderSVGDocumentToByteArray(
                        ScoreListSVGMapper.mapScoreListToBpList(player.getScoreSequences(), player.getInfo(), offset)
                )
        );
    }
    public static byte[] renderPlayerScoreListToList(List<ScoreSequence> scoreSequences) throws IOException
    {
        return cached(RenderFingerprint.of("bp-list-global").addScoreSequences(scoreSequences),
                () -> SVGRenderer.renderSVGDocumentToByteArray(
                        ScoreListSVGMapper.mapScoreListToBpList(scoreSequences, "#f8bad4", "Current Best Plays of osu! by PP Earned", 1))
        );
    }
    public static byte[] renderPlayerScoreListToCard(PlayerScoreList player, int offset, int type, String msg)
    {
        return cached(RenderFingerprint.of("bp-card")
                        .add("offset", offset)
                        .add("type", type)
                        .add("msg", msg)
                        .addPlayerScoreList(player),
                () -> SVGRenderer.renderSVGDocumentToByteArray(
                        ScoreListSVGMapper.mapScoreListToBpCard(player.getInfo(), player.getScoreVOList(), offset, type,
                                msg)
                )
        );
    }
    public static byte[] renderPlayerScoreListToCard(NoChokeListVO player, int offset, int type, String msg)
    {
        return cached(RenderFingerprint.of("bp-card-nochoke")
                        .add("offset", offset)
                        .add("type", type)
                        .add("msg", msg)
                        .addNoChoke(player),
                () -> SVGRenderer.renderSVGDocumentToByteArray(
                        ScoreListSVGMapper.mapScoreListToBpCard(player.getInfo(), player.getScoreList(), offset, type,
                                msg)
                )
        );
    }

    public static byte[] renderComparePlayerBps(ComparePlayerBpList data) throws IOException
    {
        return cached(RenderFingerprint.of("bp-compare").addCompare(data),
                () -> SVGRenderer.renderSVGDocumentToByteArray(
                        CompareScoreListSVGMapper.mapScoresToCompareScoreList(
                                data.getInfo(),
                                data.getCompareInfo(),
                                TransformerUtil.scoreTransformForArray(data.getScoreList()),
                                TransformerUtil.scoreTransformForArray(data.getCompareScoreList())
                        )
                )
        );
    }

    public static byte[] renderPlayerInfoVOToCard(PlayerInfoVO info)
    {
        return cached(RenderFingerprint.of("player-card").addPlayer(info),
                () -> SVGRenderer.renderSVGDocumentToByteArray(
                        PlayerInfoSVGMapper.mapPlayerInfoToCard(info)
                )
        );
    }

    public static byte[] renderMoelleuxCard(MoelleuxCard card)
    {
        return cached(RenderFingerprint.of("moelleux-card").addMoelleux(card),
                () -> SVGRenderer.renderSVGDocumentToByteArray(
                        PlayerInfoSVGMapper.mapPlayerInfoMoelleuxToCard(card.getInfo(), card.getPrimaryHue(), card.getIsLowSaturation(), card.getEnableWhiteMask())
                        , 2
                )
        );
    }

    public static byte[] renderMMoelleuxCardTrimmed(MoelleuxCard card, int scale)
    {
        return cached(RenderFingerprint.of("moelleux-trimmed")
                        .add("scale", scale)
                        .addMoelleux(card),
                () -> SVGRenderer.renderSVGDocumentToByteArrayPNG(
                        PlayerInfoSVGMapper.mapPlayerInfoMoelleuxToCardTrimmed(card.getInfo(), card.getPrimaryHue())
                        , scale
                )
        );
    }

    public static byte[] renderPerformancePlusCard(PerformancePlusProfile player, int type) throws IOException
    {
        if (CommonTool.shouldTriggerEaster()) return SVGRenderer.renderSVGDocumentToByteArray(
            PlusCardSVGMapper.mapPlusInfoToEaster(player.getPerformance(), player.getPlayer()), 1);
        return cached(RenderFingerprint.of("ppplus-card").add("type", type).addPlusProfile(player),
                () -> {
                    if (type == 0) return SVGRenderer.renderSVGDocumentToByteArray(
                            PlusCardSVGMapper.mapPlusInfoToCardCC2024(player.getPerformance(), player.getPlayer()),
                            1);
                    return SVGRenderer.renderSVGDocumentToByteArray(
                            PlusCardSVGMapper.mapPlusInfoToCard(player.getPerformance(), player.getPlayer()),
                            2);
                });
    }

    public static byte[] renderAddScorePanel(AddScorePlus score) throws IOException
    {
        return cached(RenderFingerprint.of("add-score").addAddScore(score),
                () -> SVGRenderer.renderSVGDocumentToByteArray(
                        PlusScoreSVGMapper.mapPlusScoreToCard(
                                score.getScorePlus(),
                                score.getScore(),
                                CommonTool.rgbToHue(CommonTool.getDominantColorArray(score.getScore())
                                )
                        )
                )
        );
    }

    public static byte[] renderProfileInfo(ProfileInfo info) throws IOException
    {
        return cached(RenderFingerprint.of("profile").addProfile(info),
                () -> SVGRenderer.renderSVGDocumentToByteArray(
                        PlayerInfoSVGMapper.mapPlayerInfoToProfilePanel(info.getInfo(), info.getTheme(), info.getBadges())
                )
        );
    }

    public static byte[] renderOsuAvatar(PlayerInfoVO info, int type) throws IOException
    {
        return cached(RenderFingerprint.of("osu-avatar").add("type", type).addPlayer(info),
                () -> {
                    if (type == 1)
                        return SVGRenderer.renderSVGDocumentToByteArray(
                                AvatarSVGMapper.mapPlayerInfoToAvatar(info,
                                        CommonTool.getDominantHueColorThief(new File(info.getAvatarUrl())),
                                        type)
                        );
                    else
                        return SVGRenderer.renderSVGDocumentToByteArray(
                                AvatarSVGMapper.mapPlayerInfoToAvatar(info,
                                        215,
                                        type));
                });
    }
    public static byte[] renderCommandUsage(CommandUsage usage)
    {
        return cached(RenderFingerprint.of("command-usage").addUsage(usage),
                () -> SVGRenderer.renderSVGDocumentToByteArray(UsageSVGMapper.mapCommandUsageToPanel(usage))
        );
    }

    private static byte[] cached(RenderFingerprint fingerprint, BitmapRenderCache.Loader loader)
    {
        BitmapRenderCache cache = renderCache;
        if (cache == null || !cache.isEnabled())
            return invoke(loader);
        return cache.getOrCompute(fingerprint.key(), loader);
    }

    private static byte[] invoke(BitmapRenderCache.Loader loader)
    {
        try
        {
            return loader.render();
        }
        catch (RuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new LazybotRuntimeException(e);
        }
    }
}
