package me.aloic.lazybot.osu.service;

import me.aloic.lazybot.parameter.BeatmapParameter;
import me.aloic.lazybot.parameter.UpdateParameter;

public interface ManageService
{
    String update(UpdateParameter params);

    String verifyBeatmap(BeatmapParameter params);
}
