package me.aloic.lazybot.osu.service;

import me.aloic.lazybot.parameter.BeatmapParameter;
import me.aloic.lazybot.parameter.GeneralParameter;
import me.aloic.lazybot.parameter.UpdateParameter;
import me.aloic.lazybot.parameter.VerifyParameter;
import org.apache.batik.dom.GenericNotation;

public interface ManageService
{
    String update(UpdateParameter params);

    String verifyBeatmap(BeatmapParameter params);

    String unlinkUser(GeneralParameter params);

    String verify(VerifyParameter params);
}
