package me.aloic.lazybot.service.Impl;

import jakarta.annotation.Resource;
import me.aloic.lazybot.entity.po.BadgeChallengeMapPO;
import me.aloic.lazybot.entity.po.BadgeChallengeSubmissionDetailsPO;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.osu.dao.mapper.ChallengeMapMapper;
import me.aloic.lazybot.osu.utils.ModCalculatorUtil;
import me.aloic.lazybot.parameter.ChallengeSubmitParameter;
import me.aloic.lazybot.service.BadgeChallengeService;
import me.aloic.lazybot.util.DataExtractor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

//todo
@Service
public class BadgeChallengeServiceImpl implements BadgeChallengeService
{
    @Resource
    private DataExtractor dataExtractor;
    @Resource
    private ChallengeMapMapper challengeMapMapper;

    public String checkUserSubmit(ChallengeSubmitParameter params)
    {
        BadgeChallengeMapPO challengeMap = challengeMapMapper.selectByBeatmapIdAndChallengeId(params.getBeatmapId(), params.getChallengeId());
        if(challengeMap == null) {
            throw new LazybotRuntimeException("没有找到指定Challenge在" + params.getBeatmapId()+"的需求");
        }
        List<ScoreLazerDTO> userScores = dataExtractor.extractBeatmapUserScoreAll(params.getBeatmapId(), params.getPlayerId(),params.getMode());
        for (ScoreLazerDTO score : userScores)
        {
            int i=0;
            if (Optional.ofNullable(score.getStatistics().getMiss()).orElse(0) <= challengeMap.getMax_accepted_miss()) {
                i++;
            }
            if (score.getAccuracy()*100.0 >= challengeMap.getRequired_acc()) {
                i++;
            }
            if (score.getMax_combo() >= challengeMap.getRequired_combo()) {
                i++;
            }
            if (ModCalculatorUtil.compareMods(score.getMods(), challengeMap.getMods_allowed())) {
                i++;
            }
            if (i == 4) {
                BadgeChallengeSubmissionDetailsPO submissionDetails = new BadgeChallengeSubmissionDetailsPO(score, params.getChallengeId());
                return "[Lazybot] 提交成功";
            }
        }
        return "[Lazybot] 很抱歉，未找到没有满足条件的成绩";
    }
    public String showAllActiveChallenges()
    {
        return null;
    }
    public String showUserParticipation()
    {
        return null;
    }
    public String searchChallenge()
    {
        return null;
    }
}
