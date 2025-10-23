package me.aloic.lazybot.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.aloic.lazybot.entity.po.BadgeDefinitionPO;
import me.aloic.lazybot.entity.po.BadgeKeyPO;
import me.aloic.lazybot.entity.po.BadgeKeyRedeemedLogPO;
import me.aloic.lazybot.entity.po.BadgeUserOwnedPO;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.mapper.*;
import me.aloic.lazybot.parameter.BadgeKeyParameter;
import me.aloic.lazybot.service.BadgeKeyService;
import me.aloic.lazybot.util.BadgeKeyGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class BadgeKeyServiceImpl implements BadgeKeyService
{
    @Resource
    private BadgeUserOwnedMapper badgeUserOwnedMapper;
    @Resource
    private BadgeDefinitionMapper badgeDefinitionMapper;
    @Resource
    private KeyRedeemedLogMapper keyRedeemedLogMapper;

    @Resource
    private BadgeKeyMapper badgeKeyMapper;

    @Transactional
    @Override
    public String generateKeyForCertainBadge(BadgeKeyParameter params)
    {
        BadgeDefinitionPO badge = badgeDefinitionMapper.selectById(params.getBadgeId());
        if (badge == null || badge.getType().equals("0")) throw new LazybotRuntimeException("无效的Badge或不允许的Badge类型");

        if (!params.getIsMultiKey())
        {
            BadgeKeyPO key = new BadgeKeyPO(BadgeKeyGenerator.generateKey(5,4),
                    params.getBadgeId(),
                    params.getMaxUses(),
                    params.getExpireTime());
            badgeKeyMapper.insert(key);
            return "[Lazybot] 成功创建多次使用Key: " + key.getCdkey() +"\n使用次数限制: " + params.getMaxUses()+"\n过期时间: "+key.getExpired_at();
        }
        else
        {
            List<BadgeKeyPO> resultKeys = new ArrayList<>();
            for (int i=0;i< params.getMaxUses();i++)
            {
                resultKeys.add(new BadgeKeyPO(BadgeKeyGenerator.generateKey(5,4),
                        params.getBadgeId(),
                        1,
                        params.getExpireTime()));
            }
            badgeKeyMapper.insertBatch(resultKeys);
            return "[Lazybot] 成功创建多个一次性Key: " + formatKeyString(resultKeys) + "\n过期时间: "+resultKeys.getFirst().getExpired_at();
        }
    }


    @Transactional
    @Override
    public String redeemBadge(Integer lazybotId, String cdkey) {
        BadgeKeyPO key = badgeKeyMapper.selectByKey(cdkey);

        if (key == null || key.getIs_active()==0) {
            return "[Lazybot] 无效的兑换码";
        }

        if (key.getExpired_at() != null && key.getExpired_at().isBefore(LocalDateTime.now())) {
            return "[Lazybot] 此兑换码已过期";
        }

        if (key.getUsed_count() >= key.getMax_uses()) {
            return "[Lazybot] 此兑换码已被兑换或无效";
        }

        BadgeDefinitionPO badge = badgeDefinitionMapper.selectById(key.getBadge_id());
        if (badge == null) {
            return "[Lazybot] 无效的奖励Badge";
        }

        if (badge.getType().equals("0")) {
            return "[Lazybot] 此Badge奖励不可兑换，因为其是保留类型";
        }

        Long exists = badgeUserOwnedMapper.selectCount(
                new QueryWrapper<BadgeUserOwnedPO>()
                        .eq("user_id", lazybotId)
                        .eq("badge_id", key.getId())
        );
        if (exists > 0) {
            return "[Lazybot] 你已经拥有此奖励";
        }

        BadgeUserOwnedPO ub = new BadgeUserOwnedPO();
        ub.setUser_id(lazybotId);
        ub.setBadge_id(key.getBadge_id());
        ub.setObtain_time(LocalDateTime.now());
        badgeUserOwnedMapper.insert(ub);

        key.setUsed_count(key.getUsed_count() + 1);
        badgeKeyMapper.updateById(key);

        BadgeKeyRedeemedLogPO log = new BadgeKeyRedeemedLogPO(key.getId(), key.getBadge_id(), lazybotId);
        keyRedeemedLogMapper.insert(log);


        return "[Lazybot] 成功兑换Badge: "+badge.getName();
    }

    private String formatKeyString(List<BadgeKeyPO> keys)
    {
        StringBuilder sb= new StringBuilder("\n");
        for (BadgeKeyPO key:keys)
        {
            sb.append(key.getCdkey()).append("\n");
        }
        return sb.toString();
    }






}
