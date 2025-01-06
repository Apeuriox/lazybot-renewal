package me.aloic.lazybot.osu.service;

import me.aloic.lazybot.parameter.GeneralParameter;

public interface TrackService
{
    byte[] ppTimeMap(GeneralParameter params) throws Exception;
}
