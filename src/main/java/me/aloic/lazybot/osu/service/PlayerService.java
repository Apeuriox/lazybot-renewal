package me.aloic.lazybot.osu.service;

import me.aloic.lazybot.parameter.*;

import java.io.IOException;

public interface PlayerService
{

    byte[] score(ScoreParameter params) throws Exception;

    byte[] recent(RecentParameter params, Integer type) throws IOException;

    byte[] bp(BpParameter params) throws Exception;

    byte[] bplistCardView(BplistParameter params) throws Exception;

    byte[] bplistListView(BplistParameter params) throws Exception;

    byte[] todayBp(TodaybpParameter params) throws Exception;

    byte[] bpvs(BpvsParameter params)throws Exception;

    byte[] noChoke(GeneralParameter params, int type) throws Exception;

    byte[] card(GeneralParameter params) throws Exception;

    byte[] profile(GeneralParameter params) throws Exception;

    String nameToId(NameToIdParameter params) throws Exception;
}
