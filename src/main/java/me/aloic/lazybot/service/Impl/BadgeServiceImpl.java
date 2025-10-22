package me.aloic.lazybot.service.Impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.aloic.lazybot.entity.message.LazybotMessageWithImage;
import me.aloic.lazybot.entity.po.BadgeDefinitionPO;
import me.aloic.lazybot.entity.po.BadgeUserOwnedPO;
import me.aloic.lazybot.entity.vo.BadgeUserVO;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.mapper.BadgeDefinitionMapper;
import me.aloic.lazybot.osu.dao.mapper.BadgeUserOwnedMapper;
import me.aloic.lazybot.osu.dao.mapper.TokenMapper;
import me.aloic.lazybot.parameter.BadgeActionParameter;
import me.aloic.lazybot.parameter.BadgeUserActionParameter;
import me.aloic.lazybot.parameter.TipsParameter;
import me.aloic.lazybot.service.BadgeService;
import me.aloic.lazybot.util.BadgeLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class BadgeServiceImpl implements BadgeService
{
    @Resource
    private BadgeUserOwnedMapper badgeUserOwnedMapper;
    @Resource
    private BadgeDefinitionMapper badgeDefinitionMapper;
    @Resource
    private TokenMapper tokenMapper;

    @Override
    public String addBadge(BadgeActionParameter params)
    {
        BadgeDefinitionPO badgeDefinitionPO;
        try{
            badgeDefinitionPO = new BadgeDefinitionPO(params);
            badgeDefinitionMapper.insert(badgeDefinitionPO);
        }
        catch (Exception e)
        {
            log.error("添加徽章时出错: {}", e.getMessage());
            throw new LazybotRuntimeException("添加失败");
        }
        return "[Lazybot] 成功创建徽章: " + params.getName() +" id: " + badgeDefinitionPO.getId();
    }
    //this func did not check whether the user got the target badge
    @Override
    public String addBadgeToUser(BadgeUserActionParameter params)
    {
        int successCount = 0;
        for (int i=0;i<params.getBadgeIds().size();i++)
        {
            Integer badgeId = params.getBadgeIds().get(i);
            Integer playerId = params.getTargetPlayerIds().get(i);
            BadgeUserOwnedPO badgeUserOwnedPO = new BadgeUserOwnedPO();
            checkBadgeAndUserExistence(badgeId, playerId);
            badgeUserOwnedPO.setBadge_id(badgeId);
            badgeUserOwnedPO.setUser_id(playerId);
            badgeUserOwnedPO.setObtain_time(LocalDateTime.now());
            try{
                badgeUserOwnedMapper.insert(badgeUserOwnedPO);
                successCount++;
            }
            catch (Exception e)
            {
                log.warn("添加徽章失败: Add {} to {}", badgeId, playerId);
            }
        }
        return "[Lazybot] 成功添加，" + "成功"+successCount+"个，失败"+(params.getBadgeIds().size()-successCount)+"个";
    }

    @Override
    @Transactional
    public String removeBadge(TipsParameter params)
    {
        if (params.getId()==0) throw new LazybotRuntimeException("请输入正确的id");
        try{
            badgeUserOwnedMapper.deleteByBadgeId(params.getId());
            badgeDefinitionMapper.deleteById(params.getId());

        }
        catch (Exception e)
        {
            throw new LazybotRuntimeException("删除失败");
        }
       return "[Lazybot] 成功删除Badge id: " +params.getId();
    }

    @Override
    public String removeBadgeFromUser(BadgeUserActionParameter params)
    {
        int successCount = 0;
        for (int i=0;i<params.getBadgeIds().size();i++)
        {
            Integer badgeId = params.getBadgeIds().get(i);
            Integer playerId = params.getTargetPlayerIds().get(i);
            checkBadgeAndUserExistence(badgeId, playerId);
            try{
                badgeUserOwnedMapper.deleteById(playerId, badgeId);
                successCount++;
            }
            catch (Exception e)
            {
                log.warn("删除徽章失败: Delete {} from {}", badgeId, playerId);
            }
        }
        return "[Lazybot] 成功删除，" + "成功"+successCount+"个，失败"+(params.getBadgeIds().size()-successCount)+"个";
    }

    // waiting fot graphic design
    public byte[] showUserAllBadge(Integer playerId)
    {
        return null;
    }

    @Override
    public String showUserAllBadgeText(Integer playerId)
    {
        return inlineBadgeToString(badgeUserOwnedMapper.selectBadgesByUserId(playerId));
    }

    @Override
    public LazybotMessageWithImage showUserOwnedSingleBadge(Integer playerId, Integer index)
    {
        List<BadgeUserOwnedPO> userBadges = badgeUserOwnedMapper.selectUserBadges(playerId);
        if (userBadges==null || userBadges.isEmpty()) throw new LazybotRuntimeException("用户还没有任何徽章呢...");
        if (index<1 || index>userBadges.size()) throw new LazybotRuntimeException("索引越界或不合法");
        BadgeDefinitionPO badge = badgeDefinitionMapper.selectById(userBadges.get(index-1).getBadge_id());
        LazybotMessageWithImage message = new LazybotMessageWithImage(inlineBadgeDescription(badge, userBadges.get(index-1)));
        try{
            message.setImage(BadgeLoader.loadBadgeImage(userBadges.get(index-1).getBadge_id()));
        }
        catch (Exception e)
        {
            log.warn("加载此Badge图片失败: {}", userBadges.get(index-1).getBadge_id());
//            throw new LazybotRuntimeException("加载Badge图片失败:" + userBadges.get(index-1).getBadge_id());
        }
        return message;
    }

    public String submitScoreToChallenge()
    {
        return null;
    }


    private String inlineBadgeToString(List<BadgeUserVO> userBadges)
    {
        StringBuilder sb = new StringBuilder("[Lazybot] 该用户拥有的Badge如下:\n");
        if (userBadges==null || userBadges.isEmpty())
        {
            throw new LazybotRuntimeException("用户还没有任何徽章呢...");
        }
        for (int i=0;i<userBadges.size();i++)
        {
            sb.append(i+1).append(". ")
                    .append(userBadges.get(i).getName())
                    .append("(").append(userBadges.get(i).getAlternative_name()).append(")")
                    .append("[").append(userBadges.get(i).getObtain_time().toLocalDate()).append("]")
                    .append("\n");
        }
        return sb.toString();
    }

    private void checkBadgeAndUserExistence(Integer badgeId,Integer playerId)
    {
        BadgeDefinitionPO badgeDefinitionPO = badgeDefinitionMapper.selectById(badgeId);
        if (badgeDefinitionPO==null) throw new LazybotRuntimeException("Badge不存在: " + badgeId);
        if (tokenMapper.selectByPlayerId(playerId)==null) throw new LazybotRuntimeException("用户不存在或未绑定: " + playerId);
    }

    private String inlineBadgeDescription(BadgeDefinitionPO badge, BadgeUserOwnedPO userOwned)
    {
        StringBuilder sb = new StringBuilder("[Lazybot] 名称: ");
                sb.append(badge.getName()).append("\n");
                if (badge.getDescription()!=null) sb.append("描述: ").append(badge.getDescription()).append("\n");
                sb.append("代称: ").append(badge.getAlternative_name()).append("\n")
                .append("获得时间: ").append(userOwned.getObtain_time().toLocalDate());
                if (userOwned.getSource_text()!=null) sb.append("\n").append("来源: ").append(userOwned.getSource_text());
        return sb.toString();
    }
}
