package me.aloic.lazybot.osu.service;

import me.aloic.lazybot.parameter.*;
import org.apache.batik.dom.GenericNotation;

public interface ManageService
{
    String update(UpdateParameter params);

    String verifyBeatmap(BeatmapParameter params);

    String unlinkUser(GeneralParameter params);

    String verify(VerifyParameter params);

    String addTips(ContentParameter params);
}
