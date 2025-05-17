package me.aloic.lazybot.osu.service;

import me.aloic.lazybot.parameter.GeneralParameter;
import me.aloic.lazybot.parameter.TipsParameter;
import me.aloic.lazybot.parameter.WhatIfParameter;

import java.nio.file.Path;

public interface FunService
{
    String tips(TipsParameter parameter);

    Path modInfo(GeneralParameter parameter);

    String whatIfIGotSomePP(WhatIfParameter whatIfParameter);
}
