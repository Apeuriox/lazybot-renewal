package me.aloic.lazybot.osu.service;

import me.aloic.lazybot.osu.dao.entity.vo.ScoreSequence;
import me.aloic.lazybot.parameter.GeneralParameter;
import me.aloic.lazybot.parameter.TopScoresParameter;

import java.io.IOException;
import java.util.List;

public interface TrackService
{
    byte[] ppTimeMap(GeneralParameter params) throws Exception;

    List<ScoreSequence> bestPlaysInGamemode(TopScoresParameter params) throws IOException;
}
