package me.aloic.lazybot.osu.service;

import me.aloic.lazybot.parameter.GeneralParameter;
import me.aloic.lazybot.parameter.TopScoresParameter;

import java.io.IOException;

public interface TrackService
{
    byte[] ppTimeMap(GeneralParameter params) throws Exception;

    byte[] bestPlaysInGamemode(TopScoresParameter params) throws IOException;
}
