package me.aloic.lazybot.graphics.render;

import me.aloic.lazybot.entity.command.*;
import me.aloic.lazybot.entity.vo.ThumbnailClassicalVO;
import me.aloic.lazybot.graphics.mapping.documentMapper.*;
import me.aloic.lazybot.osu.dao.entity.vo.NoChokeListVO;
import me.aloic.lazybot.osu.dao.entity.vo.PPPlusScore;
import me.aloic.lazybot.osu.dao.entity.vo.PlayerInfoVO;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import me.aloic.lazybot.util.CommonTool;
import me.aloic.lazybot.util.TransformerUtil;

import java.io.File;
import java.io.IOException;

public class RendererDistributor
{

    public static byte[] renderScoreVOToImage(ScoreVO score, int version) throws IOException
    {
        return SVGRenderer.renderSVGDocumentToByteArray(
                ScoreSVGMapper.renderScoreToImage(score, version, CommonTool.getDominantColorArray(score))
        );
    }


    public static byte[] renderPPPlusScoreToQuadraGrid(PPPlusScore scorePlus) throws IOException
    {
        return SVGRenderer.renderSVGDocumentToByteArray(
                PlusScoreSVGMapper.mapPlusScoreToQuadraGrid(
                        scorePlus, CommonTool.getDominantHueColorThief(new File(scorePlus.getBeatmap().getBgUrl()))
                )
        );
    }


    public static byte[] renderThumbnailClassical(ThumbnailClassicalVO tcData)
    {
        return SVGRenderer.renderSVGDocumentToByteArray(
                ThumbnailSVGMapper.mapToThumbnailClassical(tcData));
    }


    public static byte[] renderMapScore(UserAllScore uas, Boolean ignoreBanner)
    {
        return SVGRenderer.renderSVGDocumentToByteArray(
                MapScoreSVGMapper.mapMapScoreListToAllScorePanel(uas.getMapScoreList(),uas.getBeatmapPerformance(), false),
                2f);
    }
    public static byte[] renderPlayerScoreListToCard(PlayerScoreList player, int offset, int type)
    {
        return SVGRenderer.renderSVGDocumentToByteArray(
                ScoreListSVGMapper.mapScoreListToBpCard(player.getInfo(),player.getScoreVOList(), offset,type)
        );
    }
    public static byte[] renderPlayerScoreListToCard(NoChokeListVO player, int offset, int type)
    {
        return SVGRenderer.renderSVGDocumentToByteArray(
                ScoreListSVGMapper.mapScoreListToBpCard(player.getInfo(),player.getScoreList(), offset,type)
        );
    }
    public static byte[] renderPlayerScoreListToList(PlayerScoreList player, int offset) throws IOException
    {
        return SVGRenderer.renderSVGDocumentToByteArray(
                ScoreListSVGMapper.mapScoreListToBpList(player.getScoreSequences(),player.getInfo(), offset)
        );
    }
    public static byte[] renderPlayerScoreListToCard(PlayerScoreList player, int offset, int type, String msg)
    {
        return SVGRenderer.renderSVGDocumentToByteArray(
                ScoreListSVGMapper.mapScoreListToBpCard(player.getInfo(),player.getScoreVOList(),offset,type,
                        msg)
        );
    }
    public static byte[] renderPlayerScoreListToCard(NoChokeListVO player, int offset, int type, String msg)
    {
        return SVGRenderer.renderSVGDocumentToByteArray(
                ScoreListSVGMapper.mapScoreListToBpCard(player.getInfo(),player.getScoreList(),offset,type,
                        msg)
        );
    }

    public static byte[] renderComparePlayerBps(ComparePlayerBpList data) throws IOException
    {
        return SVGRenderer.renderSVGDocumentToByteArray(
                CompareScoreListSVGMapper.mapScoresToCompareScoreList(
                        data.getInfo(),
                        data.getCompareInfo(),
                        TransformerUtil.scoreTransformForArray(data.getScoreList()),
                        TransformerUtil.scoreTransformForArray(data.getCompareScoreList())
                )
        );
    }

    public static byte[] renderPlayerInfoVOToCard(PlayerInfoVO info)
    {
        return SVGRenderer.renderSVGDocumentToByteArray(
                PlayerInfoSVGMapper.mapPlayerInfoToCard(info)
        );
    }

    public static byte[] renderMoelleuxCard(MoelleuxCard card)
    {
        return SVGRenderer.renderSVGDocumentToByteArray(
                PlayerInfoSVGMapper.mapPlayerInfoMoelleuxToCard(card.getInfo(), card.getPrimaryHue(), card.getIsLowSaturation(), card.getEnableWhiteMask())
                ,2
        );
    }

    public static byte[] renderMMoelleuxCardTrimmed(MoelleuxCard card)
    {
        return SVGRenderer.renderSVGDocumentToByteArrayPNG(
                PlayerInfoSVGMapper.mapPlayerInfoMoelleuxToCardTrimmed(card.getInfo(), card.getPrimaryHue())
                ,1
        );
    }

    public static byte[] renderPerformancePlusCard(PerformancePlusProfile player, int type) throws IOException
    {
        if (type==1) return SVGRenderer.renderSVGDocumentToByteArray(
                PlusCardSVGMapper.mapPlusInfoToCardCC2024(player.getPerformance(),player.getPlayer()),
                1);
        return SVGRenderer.renderSVGDocumentToByteArray(
                PlusCardSVGMapper.mapPlusInfoToCard(player.getPerformance(),player.getPlayer()),
                2);
    }

    public static byte[] renderAddScorePanel(AddScorePlus score) throws IOException
    {
        return SVGRenderer.renderSVGDocumentToByteArray(
                PlusScoreSVGMapper.mapPlusScoreToCard(score.getScorePlus(), score.getScore(), CommonTool.rgbToHue(CommonTool.getDominantColorArray(score.getScore()))
                ));
    }

    public static byte[] renderProfileInfo(ProfileInfo info) throws IOException
    {
        return SVGRenderer.renderSVGDocumentToByteArray(
                PlayerInfoSVGMapper.mapPlayerInfoToProfilePanel(info.getInfo(), info.getTheme(), info.getBadges())
        );
    }

    public static byte[] renderOsuAvatar(PlayerInfoVO info, int type) throws IOException
    {
        if (type==1)
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
    }

}
