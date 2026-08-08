package me.aloic.lazybot.service.Impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.aloic.lazybot.entity.message.LazybotMessageWithImage;
import me.aloic.lazybot.entity.po.BadgeDefinitionPO;
import me.aloic.lazybot.entity.po.BadgeUserOwnedPO;
import me.aloic.lazybot.entity.po.BadgeUserShowcasePO;
import me.aloic.lazybot.entity.vo.BadgeUserVO;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.mapper.BadgeDefinitionMapper;
import me.aloic.lazybot.osu.dao.mapper.BadgeShowcaseMapper;
import me.aloic.lazybot.osu.dao.mapper.BadgeUserOwnedMapper;
import me.aloic.lazybot.osu.dao.mapper.UserBindingMapper;
import me.aloic.lazybot.parameter.*;
import me.aloic.lazybot.service.BadgeService;
import me.aloic.lazybot.util.BadgeLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private UserBindingMapper userBindingMapper;
    @Resource
    private BadgeShowcaseMapper showcaseMapper;

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
        return "[Lazybot] 成功创建徽章: " + params.getName() +", ID为: " + badgeDefinitionPO.getId();
    }
    @Override
    public String addBadgeImageRemote(BadgeImageParameter params)
    {
        BadgeDefinitionPO badgeDefinitionPO = badgeDefinitionMapper.selectById(params.getBadgeId());
        if (badgeDefinitionPO==null) throw new LazybotRuntimeException("目标Badge不存在: " +params.getBadgeId());
        try
        {
            log.info("开始更新Badge图片: {}", params.getBadgeId());
            if (!(params.getTargetUrl().startsWith("http://") || params.getTargetUrl().startsWith("https://")))
                throw new LazybotRuntimeException("超链接协议无效");
            BadgeLoader.badgeImageCacheDownload(params);
            badgeDefinitionPO.setRemote_url(params.getTargetUrl());
            badgeDefinitionMapper.updateImage(badgeDefinitionPO);
        }
        catch (Exception e)
        {
            log.error("更新Badge图片链接时出错: {}", e.getMessage());
            throw new LazybotRuntimeException("更新失败: " + e.getMessage());
        }
        return "[Lazybot] 成功更新徽章" + params.getBadgeId() +"的图片";
    }

    //this func did not check whether the user got the target badge
    @Override
    public String addBadgeToUser(BadgeUserActionParameter params)
    {
        List<Integer> failedIds = new ArrayList<>();
        int successCount = 0;
        for (int i=0;i<params.getBadgeIds().size();i++)
        {

            Integer badgeId = params.getBadgeIds().get(i);
            Integer lazybotId = params.getTargetLazybotIds().get(i);
            BadgeUserOwnedPO badgeUserOwnedPO = new BadgeUserOwnedPO();
            checkBadgeAndUserExistence(badgeId, lazybotId);
            badgeUserOwnedPO.setBadge_id(badgeId);
            badgeUserOwnedPO.setUser_id(lazybotId);
            badgeUserOwnedPO.setObtain_time(LocalDateTime.now());
            try{
                badgeUserOwnedMapper.insert(badgeUserOwnedPO);
                successCount++;
            }
            catch (Exception e)
            {
                failedIds.add(lazybotId);
                log.warn("添加徽章失败: Add {} to {}", badgeId, lazybotId);
            }
        }
        return "[Lazybot] 成功执行添加任务，" + "成功"+successCount+"个，失败"+(params.getBadgeIds().size()-successCount)+"个" + failedIdString(failedIds);
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
       return "[Lazybot] 成功执行删除Badge任务，操作对象IO: " +params.getId();
    }

    @Override
    public String removeBadgeFromUser(BadgeUserActionParameter params)
    {
        List<Integer> failedIds = new ArrayList<>();
        int successCount = 0;
        for (int i=0;i<params.getBadgeIds().size();i++)
        {
            Integer badgeId = params.getBadgeIds().get(i);
            Integer lazybotId = params.getTargetLazybotIds().get(i);
            checkBadgeAndUserExistence(badgeId, lazybotId);
            try{
                badgeUserOwnedMapper.deleteById(lazybotId, badgeId);
                successCount++;
            }
            catch (Exception e)
            {
                failedIds.add(lazybotId);
                log.warn("删除徽章失败: Delete {} from {}", badgeId, lazybotId);
            }
        }
        return "[Lazybot] 成功执行删除任务，" + "成功"+successCount+"个，失败"+(params.getBadgeIds().size()-successCount)+"个" + failedIdString(failedIds);
    }

    // waiting fot graphic design
    public byte[] showUserAllBadge(Integer lazybotId)
    {
        return null;
    }

    @Override
    public String showUserAllBadgeText(Integer lazybotId)
    {
        return inlineBadgeToString(badgeUserOwnedMapper.selectBadgesByUserId(lazybotId));
    }

    @Override
    public LazybotMessageWithImage showUserOwnedSingleBadge(Integer lazybotId, Integer index)
    {
        List<BadgeUserOwnedPO> userBadges = badgeUserOwnedMapper.selectUserBadges(lazybotId);
        if (userBadges==null || userBadges.isEmpty()) throw new LazybotRuntimeException("用户还没有任何徽章呢...");
        if (index<1 || index>userBadges.size()) throw new LazybotRuntimeException("索引越界或不合法");
        BadgeDefinitionPO badge = badgeDefinitionMapper.selectById(userBadges.get(index-1).getBadge_id());
        LazybotMessageWithImage message = new LazybotMessageWithImage(inlineBadgeDescription(badge, userBadges.get(index-1)));
        try{
            message.setImage(BadgeLoader.loadBadgeImage(userBadges.get(index-1).getBadge_id()));
            if (message.getImage()==null) {
                BadgeLoader.badgeImageCacheDownload(userBadges.get(index-1).getBadge_id(),badge.getRemote_url());
                message.setImage(BadgeLoader.loadBadgeImage(userBadges.get(index-1).getBadge_id()));
            }
        }
        catch (Exception e)
        {
            log.warn("加载此Badge图片失败: {}", userBadges.get(index-1).getBadge_id());
//            throw new LazybotRuntimeException("加载Badge图片失败:" + userBadges.get(index-1).getBadge_id());
        }
        return message;
    }

    @Override
    @Transactional
    public String userSetShowcaseBadge(Integer lazybotId, List<Integer> indexes)
    {
        if (indexes==null || indexes.isEmpty()) throw new LazybotRuntimeException("请输入正确的索引...");
        if (indexes.size()>4) throw new LazybotRuntimeException("最多只能设置4个Badge");
        List<BadgeUserShowcasePO> userSetBadges = new ArrayList<>();
        List<BadgeUserVO> ownedBadges = badgeUserOwnedMapper.selectBadgesByUserId(lazybotId);
        if (ownedBadges==null || ownedBadges.isEmpty()) throw new LazybotRuntimeException("用户还没有任何徽章呢...");
        for (int index: indexes)
        {
            try{
                userSetBadges.add(new BadgeUserShowcasePO(ownedBadges.get(index-1).getId(), lazybotId));
            }
            catch (Exception e)
            {
                throw new LazybotRuntimeException("索引越界或不合法");
            }
        }
        showcaseMapper.deleteAllUser(lazybotId);
        showcaseMapper.insertBatch(userSetBadges);
        return "[Lazybot] 成功设置展示，共设置"+indexes.size()+"个";
    }

    @Transactional
    @Override
    public String clearUserShowcaseBadge(Integer lazybotId)
    {
        showcaseMapper.deleteAllUser(lazybotId);
        return "[Lazybot] 成功清除";
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
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

    private void checkBadgeAndUserExistence(Integer badgeId,Integer lazybotId)
    {
        BadgeDefinitionPO badgeDefinitionPO = badgeDefinitionMapper.selectById(badgeId);
        if (badgeDefinitionPO==null) throw new LazybotRuntimeException("此Badge ID不存在: " + badgeId);
        if (userBindingMapper.selectByLazybotUserId(lazybotId, "bancho")==null) throw new LazybotRuntimeException("操作失败:用户不存在或未绑定，LazybotID: " + lazybotId);
    }

    public static String inlineBadgeDescription(BadgeDefinitionPO badge, BadgeUserOwnedPO userOwned)
    {
        StringBuilder sb = new StringBuilder("[Lazybot] 名称: ");
                sb.append(badge.getName()).append("\n");
                if (badge.getDescription()!=null) sb.append("描述: ").append(badge.getDescription()).append("\n");
                sb.append("代称: ").append(badge.getAlternative_name()).append("\n")
                .append("获得时间: ").append(userOwned.getObtain_time().toLocalDate());
                if (userOwned.getSource_text()!=null) sb.append("\n").append("来源: ").append(userOwned.getSource_text());
        return sb.toString();
    }

    private String failedIdString(List<Integer> failedIds)
    {
        if (failedIds==null || failedIds.isEmpty()) return "";
        StringBuilder sb = new StringBuilder(", 失败ID列表为: ");
        for (Integer id:failedIds)
        {
            sb.append(id).append(", ");
        }
        sb.deleteCharAt(sb.length()-2);
        return sb.toString();
    }

}
