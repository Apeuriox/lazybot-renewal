package me.aloic.lazybot.service.Impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.aloic.lazybot.entity.message.LazybotMessageWithImage;
import me.aloic.lazybot.entity.po.*;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.osu.dao.mapper.*;
import me.aloic.lazybot.osu.utils.ModCalculatorUtil;
import me.aloic.lazybot.parameter.ChallengeSubmitParameter;
import me.aloic.lazybot.service.BadgeChallengeService;
import me.aloic.lazybot.util.BadgeLoader;
import me.aloic.lazybot.util.CommonTool;
import me.aloic.lazybot.util.DataExtractor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

//todo
@Slf4j
@Service
public class BadgeChallengeServiceImpl implements BadgeChallengeService
{
    @Resource
    private DataExtractor dataExtractor;
    @Resource
    private ChallengeMapMapper challengeMapMapper;
    @Resource
    private ChallengeSubmissionLogMapper challengeSubmissionLogMapper;
    @Resource
    private BadgeUserOwnedMapper badgeUserOwnedMapper;
    @Resource
    private BadgeChallengeMapper challengeMapper;
    @Resource
    private BadgeDefinitionMapper badgeDefinitionMapper;
    @Resource
    private UserBindingMapper userBindingMapper;

    @Transactional
    @Override
    public String checkUserSubmit(ChallengeSubmitParameter params)
    {
        BadgeChallengeMapPO challengeMap = challengeMapMapper.selectByBeatmapIdAndChallengeId(params.getBeatmapId(), params.getChallengeId());
        if(challengeMap == null) {
            throw new LazybotRuntimeException("没有找到指定Challenge在" + params.getBeatmapId()+"的需求，请检查输入");
        }
        List<ScoreLazerDTO> userScores = dataExtractor.extractBeatmapUserScoreAll(params.getBeatmapId(), params.getPlayerId(),params.getMode());
        for (ScoreLazerDTO score : userScores)
        {
            if (!CommonTool.isEmpty(score.getMods())) {
                score.setMods(score.getMods().stream().filter(mod -> !mod.getAcronym().equals("CL")).toList());
            }
            int i=0;
            if (Optional.ofNullable(score.getStatistics().getMiss()).orElse(0) <= challengeMap.getMax_accepted_miss()) {
                i++;
            }
            if (score.getAccuracy() >= challengeMap.getRequired_acc()) {
                i++;
            }
            if (score.getMax_combo() >= challengeMap.getRequired_combo()) {
                i++;
            }
            if (ModCalculatorUtil.compareMods(score.getMods(), challengeMap.getMods_allowed())) {
                i++;
            }
            if (i == 4) {
                BadgeChallengeSubmissionDetailsPO existingSubmission = challengeSubmissionLogMapper.selectByPlayerIdAndStats(params.getPlayerId(),
                        params.getBeatmapId(),
                        params.getChallengeId());
                if (existingSubmission != null) {
                    return "[Lazybot] 您已满足该成绩需求";
                }
                BadgeChallengeSubmissionDetailsPO submissionDetails = new BadgeChallengeSubmissionDetailsPO(score, params.getChallengeId());
                challengeSubmissionLogMapper.insert(submissionDetails);
                try{
                    return checkUserChallengeCompletion(params.getChallengeId(), params.getPlayerId(), params.getLazybotId());
                }
                catch (Exception e)
                {
                    log.info(e.getMessage());
                }
                return "[Lazybot] 接受成绩 " + score.getId() +"，提交成功";
            }
        }
        return "[Lazybot] 很抱歉，未找到没有满足条件的成绩";
    }



    @Override
    public String createChallengeRequirement(ChallengeSubmitParameter params)
    {

        return null;
    }

    @Override
    public List<LazybotMessageWithImage> showAllActiveChallenges() throws IOException
    {
        List<BadgeChallengeDefinitionPO> challengeMap = challengeMapper.selectAllActive();
        List<LazybotMessageWithImage> messageList =new ArrayList<>();
        if (CommonTool.isEmpty(challengeMap)) {
            messageList.add(new LazybotMessageWithImage("[Lazybot] 当前没有活跃的Challenge"));
            return messageList;
        }
        messageList.add(new LazybotMessageWithImage("[Lazybot] 当前的活跃Challenge有: \n"));
        for (int i=0;i<challengeMap.size();i++) {
            LazybotMessageWithImage message = new LazybotMessageWithImage((i+1) +". " + challengeMap.get(i).toLazybotString());
            message.setImage(BadgeLoader.loadBadgeImage(challengeMap.get(i).getBadge_id()));
            messageList.add(message);
        }
       return messageList;
    }

    @Override
    public LazybotMessageWithImage showRequirementsInChallenge(int challengeId) throws IOException
    {
        BadgeChallengeDefinitionPO challenge = challengeMapper.selectById(challengeId);
        if (challenge == null) {
            return new LazybotMessageWithImage("[Lazybot] 没有找到指定Challenge");
        }
        List<BadgeChallengeMapPO> challengeMap = challengeMapMapper.selectByChallengeId(challengeId);
        if (CommonTool.isEmpty(challengeMap)) {
            return new LazybotMessageWithImage("[Lazybot] 指定Challenge存在但没有需求");
        }
        StringBuilder sb=new StringBuilder(challenge.getName()).append("\n").append("需求如下: \n");
        for (int i=0;i<challengeMap.size();i++) {
           sb.append(i+1).append(". ").append(challengeMap.get(i).toLazybotString());
        }
        return new LazybotMessageWithImage(BadgeLoader.loadBadgeImage(challenge.getBadge_id()), sb.toString());
    }

    public String showUserParticipation()
    {
        return null;
    }
    public String searchChallenge()
    {
        return null;
    }

    @Transactional
    public String checkUserChallengeCompletion(Integer challengeId, Integer playerId, Integer lazybotId)
    {
        List<BadgeChallengeMapPO> challengeMap = challengeMapMapper.selectByChallengeId(challengeId);
        List<BadgeChallengeSubmissionDetailsPO> submissions = challengeSubmissionLogMapper.selectByPlayerIdAndChallengeId(playerId, challengeId);
        if (CommonTool.isEmpty(challengeMap) || CommonTool.isEmpty(submissions)) {
            throw new LazybotRuntimeException("没有找到指定Challenge");
        }
        if (challengeMap.size()!= submissions.size()) {
            throw new LazybotRuntimeException("Challenge未完成，跳过颁发徽章");
        }
        int i=0;
        for (BadgeChallengeMapPO map : challengeMap)
        {
            for (BadgeChallengeSubmissionDetailsPO submission : submissions)
            {
                if (Objects.equals(map.getBeatmap_id(), submission.getBeatmap_id())) {
                    i++;
                }
            }
        }
        if (i == challengeMap.size()) {
            BadgeChallengeDefinitionPO challenge = challengeMapper.selectById(challengeId);
            BadgeUserOwnedPO badgeUserOwnedPO = new BadgeUserOwnedPO();
            checkBadgeAndUserExistence(challenge.getBadge_id(), lazybotId);
            badgeUserOwnedPO.setBadge_id(challenge.getBadge_id());
            badgeUserOwnedPO.setUser_id(lazybotId);
            badgeUserOwnedPO.setObtain_time(LocalDateTime.now());
            badgeUserOwnedPO.setSource_challenge_id(challengeId);
            badgeUserOwnedPO.setSource_text("完成" + challenge.getName() +"挑战获得");
            badgeUserOwnedMapper.insert(badgeUserOwnedPO);
            return "[Lazybot] 恭喜你获得" + challenge.getName() + "徽章";
        }
        throw new LazybotRuntimeException("不满足条件");
    }

    private void checkBadgeAndUserExistence(Integer badgeId,Integer lazybotId)
    {
        BadgeDefinitionPO badgeDefinitionPO = badgeDefinitionMapper.selectById(badgeId);
        if (badgeDefinitionPO==null) throw new LazybotRuntimeException("此Badge ID不存在: " + badgeId);
        if (userBindingMapper.selectByLazybotUserId(lazybotId, "bancho")==null) throw new LazybotRuntimeException("操作失败:用户不存在或未绑定，LazybotID: " + lazybotId);
    }
}
