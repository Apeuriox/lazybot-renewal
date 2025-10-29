package me.aloic.lazybot.osu.service;

import me.aloic.lazybot.entity.command.PlayerScoreList;
import me.aloic.lazybot.parameter.BpifParameter;
import me.aloic.lazybot.parameter.GeneralParameter;

import java.io.IOException;

public interface AnalysisService
{

    PlayerScoreList bpIf(BpifParameter params) throws IOException;

    String recommendedDifficulty(GeneralParameter params) throws Exception;
}
