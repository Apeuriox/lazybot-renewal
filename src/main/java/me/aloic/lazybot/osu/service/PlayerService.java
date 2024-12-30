package me.aloic.lazybot.osu.service;

import me.aloic.lazybot.parameter.*;

public interface PlayerService
{

    byte[] score(ScoreParameter params) throws Exception;

    byte[] recent(RecentParameter params, Integer type);

    byte[] bp(BpParameter params) throws Exception;

    byte[] bplist(BplistParameter params) throws Exception;

    byte[] todayBp(TodaybpParameter params) throws Exception;

    byte[] noChoke(NoChokeParameter params, int type) throws Exception;
}
