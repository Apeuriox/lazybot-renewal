package me.aloic.lazybot.osu.service;

import desu.life.RosuFFI;
import me.aloic.lazybot.entity.command.*;
import me.aloic.lazybot.entity.vo.ThumbnailClassicalVO;
import me.aloic.lazybot.osu.dao.entity.vo.*;
import me.aloic.lazybot.parameter.*;

import java.io.IOException;

public interface PlayerService
{

    ScoreVO getUserHighestScoreOnMap(ScoreParameter params) throws Exception;

    PPPlusScore getUserHighestScoreOnMapPlus(ScoreParameter params) throws Exception;

    UserAllScore getUserAllScoresOnMap(ScoreParameter params) throws Exception;

    BeatmapStatistics getBeatmapStatisticsWithImaginaryParams(BeatmapStatisticsParameter params) throws Exception;

    ThumbnailClassicalVO thumbnailClassicalScore(ThumbnailParameter params);

    ThumbnailClassicalVO thumbnailClassicalRecent(ThumbnailParameter params);

    ScoreVO getUserRecentScoreList(RecentParameter params, int type) throws IOException;

    PPPlusScore getUserRecentScoreListPlus(RecentParameter params, int type) throws IOException, RosuFFI.FFIException;

    ScoreVO getUserBestPerformanceSingle(BpParameter params) throws Exception;

    ScoreVO getUserBestPerformanceSingleStarMoon(BpParameter params);

    PPPlusScore getUserBestPerformanceSinglePlus(BpParameter params) throws IOException, RosuFFI.FFIException;

    PlayerScoreList bplistCardView(BplistParameter params) throws Exception;

    PlayerScoreList bplistListView(BplistParameter params) throws Exception;

    PlayerScoreList bplistCardViewStarMoon(BplistParameter params);

    PlayerScoreList bpScoreFilter(ScoreFilterParameter params) throws Exception;

    PlayerScoreList playRecentSeries(SeriesParameter params, int type, int style) throws Exception;

    PlayerScoreList getPlayerTodayNewBps(TodaybpParameter params) throws Exception;

    ComparePlayerBpList bpvs(BpvsParameter params)throws Exception;

    NoChokeListVO noChoke(GeneralParameter params, int type) throws Exception;

    PlayerScoreList noReading(GeneralParameter params) throws Exception;

    PlayerScoreList maxReading(GeneralParameter params) throws Exception;

    PlayerInfoVO getPlayerInfoVO(GeneralParameter params) throws Exception;

    MoelleuxCard cardMoelleux(CardMoelleuxParameter params) throws Exception;

    MoelleuxCard cardMoelleuxTrimmed(CardMoelleuxParameter params) throws Exception;

    PerformancePlusProfile getPerformancePlusPlayerInfo(GeneralParameter params) throws Exception;

    PlusScorePerformance getPerformanceDimensionList(PlusListParameter params);

    AddScorePlus addScoreForPerformancePlus(ScoreParameter params);

    ProfileInfo profile(ProfileParameter params) throws Exception;

    String nameToId(NameToIdParameter params) throws Exception;

    UserAllScore scoreRank(ScoreParameter params) throws Exception;
}
