package me.aloic.lazybot.service.Impl;

import jakarta.annotation.Resource;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.service.BadgeChallengeService;
import me.aloic.lazybot.util.DataExtractor;
import org.springframework.stereotype.Service;

import java.util.List;

//todo
@Service
public class BadgeChallengeServiceImpl implements BadgeChallengeService
{
    @Resource
    private DataExtractor dataExtractor;

    public String checkUserSubmit()
    {
//        List<ScoreLazerDTO> userScores = dataExtractor.extractBeatmapUserScoreAll();
        return null;
    }
    public String showAllActiveChallenges()
    {
        return null;
    }
    public String showUserParticipation()
    {
        return null;
    }
}
