package me.aloic.lazybot.osu.service;

import me.aloic.lazybot.parameter.BpifParameter;

import java.io.IOException;

public interface AnalysisService
{

    byte[] bpIf(BpifParameter params) throws IOException;
}
