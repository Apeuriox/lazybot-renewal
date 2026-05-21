package me.aloic.lazybot.osu.service;

import me.aloic.lazybot.osu.dao.entity.po.CommandUsage;
import me.aloic.lazybot.parameter.*;
import org.apache.batik.dom.GenericNotation;

import java.util.List;

public interface ManageService
{
    String update(UpdateParameter params);

    String verifyBeatmap(BeatmapParameter params);

    String updateBeatmapBackground(BeatmapParameter params);

    String unlinkUser(GeneralParameter params);

    String verify(VerifyParameter params);

    String addTips(ContentParameter params);

    String ppTest(ScoreParameter params, Long userIdentity);

    CommandUsage commandUsage();

    String annualCommandUsage();

    String plusServerStats(StatsParameter params);
}
