package me.aloic.lazybot.osu.service;

import desu.life.RosuFFI;
import me.aloic.lazybot.parameter.*;

import java.io.IOException;

public interface PlayerService
{

    byte[] score(ScoreParameter params) throws Exception;

    byte[] scorePlus(ScoreParameter params) throws Exception;

    byte[] allScore(ScoreParameter params) throws Exception;

    byte[] thumbnailClassicalScore(ThumbnailParameter params);

    byte[] thumbnailClassicalRecent(ThumbnailParameter params);

    byte[] recent(RecentParameter params, int type) throws IOException;

    byte[] recentPlus(RecentParameter params, int type) throws IOException, RosuFFI.FFIException;

    byte[] bp(BpParameter params) throws Exception;

    byte[] bpPlus(BpParameter params) throws IOException, RosuFFI.FFIException;

    byte[] bplistCardView(BplistParameter params) throws Exception;

    byte[] bplistListView(BplistParameter params) throws Exception;

    byte[] bpScoreFilter(ScoreFilterParameter params) throws Exception;

    byte[] playRecentSeries(GeneralParameter params, int type, int style) throws Exception;

    byte[] todayBp(TodaybpParameter params) throws Exception;

    byte[] bpvs(BpvsParameter params)throws Exception;

    byte[] noChoke(GeneralParameter params, int type) throws Exception;

    byte[] card(GeneralParameter params) throws Exception;

    byte[] cardMoelleux(CardMoelleuxParameter params) throws Exception;

    byte[] cardMoelleuxTrimmed(CardMoelleuxParameter params) throws Exception;

    byte[] performancePlus(GeneralParameter params) throws Exception;

    byte[] addScoreForPerformancePlus(ScoreParameter params);

    byte[] profile(ProfileParameter params) throws Exception;

    String nameToId(NameToIdParameter params) throws Exception;

    byte[] avatar(GeneralParameter params, int type) throws Exception;
}
