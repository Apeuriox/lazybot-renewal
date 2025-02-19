package me.aloic.lazybot.osu.service.ServiceImpl;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Resource;
import me.aloic.lazybot.monitor.ResourceMonitor;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.dao.entity.po.ProfileCustomizationPO;
import me.aloic.lazybot.osu.dao.mapper.CustomizationMapper;
import me.aloic.lazybot.osu.service.CustomizeService;
import me.aloic.lazybot.osu.utils.AssertDownloadUtil;
import me.aloic.lazybot.parameter.CustomizationParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommonTool;
import me.aloic.lazybot.util.DataObjectExtractor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class CustomizeServiceImpl implements CustomizeService
{
    private static final Map<String, Function<CustomizationParameter,String>> CUSTOMIZATION_MAP;
    private static final String UNVERIFIED_PATH;

    @Resource
    private CustomizationMapper customizationMapper;

    static{
        CUSTOMIZATION_MAP = Map.of("profileBG",CustomizeServiceImpl::profileBackgroundCustomize);
        UNVERIFIED_PATH = ResourceMonitor.getResourcePath().toAbsolutePath()+"osuFiles\\playerCustomization\\profile\\unverified";
    }
    @Override
    public String customize(CustomizationParameter params)
    {
        if(CUSTOMIZATION_MAP.containsKey(params.getType()))
        {
            try {
                CUSTOMIZATION_MAP.get(params.getType()).apply(params);
                insertProfileCustomizeToTable(params);
            }
            catch (Exception e) {
                throw new RuntimeException("创建客制化请求失败");
            }
        }
        return "已提交对"+params.getPlayerId()+"的背景图片修改请求，请等待验证";
    }
    private static String profileBackgroundCustomize(CustomizationParameter params)
    {
        try{
            AssertDownloadUtil.downloadResourceQueue(params.getTargetUrl(), UNVERIFIED_PATH + params.getPlayerId()  +".jpg");
        }
        catch (Exception e) {
            throw new RuntimeException("指定图片链接无效");
        }
        return "已提交对"+params.getPlayerId()+"的背景图片修改请求，请等待验证";
    }
    private void insertProfileCustomizeToTable(CustomizationParameter params) throws IOException
    {
        ProfileCustomizationPO profile = new ProfileCustomizationPO();
        profile.setPlayer_id(params.getPlayerId());
        profile.setPlayer_name(params.getPlayerName());
        profile.setQq_code(params.getQqCode());
        profile.setVerified(0);
        profile.setPreferred_type(0);
        profile.setHue(CommonTool.getDominantHueColorThief(new File(ResourceMonitor.getResourcePath().toAbsolutePath()+ "/osuFiles/playerCustomization/profile/unverified" + params.getPlayerId() +".jpg")));
        Optional.ofNullable(customizationMapper.selectById(params.getPlayerId()))
                .ifPresentOrElse(
                        userToken -> customizationMapper.updateVerifiedAndHue(0,profile.getHue(),profile.getPlayer_id()),
                        () -> customizationMapper.insert(profile)
                );

    }}
