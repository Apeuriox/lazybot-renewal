package me.aloic.lazybot.osu.service.ServiceImpl;

import jakarta.annotation.Resource;
import me.aloic.lazybot.component.SlashCommandProcessor;
import me.aloic.lazybot.monitor.ResourceMonitor;
import me.aloic.lazybot.osu.dao.entity.po.ProfileCustomizationPO;
import me.aloic.lazybot.osu.dao.mapper.CustomizationMapper;
import me.aloic.lazybot.osu.service.CustomizeService;
import me.aloic.lazybot.osu.utils.AssertDownloadUtil;
import me.aloic.lazybot.parameter.CustomizationParameter;
import me.aloic.lazybot.util.CommonTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class CustomizeServiceImpl implements CustomizeService
{
    private static final Map<String, Function<CustomizationParameter,String>> CUSTOMIZATION_MAP;
    private static final String PROFILE_RELATIVE_PATH;

    @Resource
    private CustomizationMapper customizationMapper;

    private static final Logger logger = LoggerFactory.getLogger(CustomizeServiceImpl.class);

    static{
        CUSTOMIZATION_MAP = Map.of("profilebg",CustomizeServiceImpl::profileBackgroundCustomize);
        PROFILE_RELATIVE_PATH = "\\osuFiles\\playerCustomization\\profile\\";
    }
    @Override
    public String customize(CustomizationParameter params)
    {
        if(CUSTOMIZATION_MAP.containsKey(params.getType().toLowerCase().trim())) {
            try {
                logger.info("开始处理Profile BG更改请求: {}", params);
                CUSTOMIZATION_MAP.get(params.getType().toLowerCase().trim()).apply(params);
                insertProfileCustomizeToTable(params);
            }
            catch (Exception e) {
                throw new RuntimeException("创建Profile客制化请求失败: " + e.getMessage());
            }
        }
        else {
            throw new RuntimeException("未知的客制化类型: " + params.getType());
        }
        return "已提交对"+params.getPlayerId()+"的背景图片修改请求，请等待验证";
    }
    private static String profileBackgroundCustomize(CustomizationParameter params)
    {
        try{
            String desiredSavePath = ResourceMonitor.getResourcePath().toAbsolutePath()+ PROFILE_RELATIVE_PATH + params.getPlayerId()  +".jpg";
            AssertDownloadUtil.downloadResourceQueue(params.getTargetUrl(), desiredSavePath);
            CommonTool.cropAndResize(desiredSavePath,1900,1000);
        }
        catch (Exception e) {
            throw new RuntimeException("指定图片链接无效");
        }
        return "已提交对"+params.getPlayerId()+"的背景图片修改请求，请等待验证";
    }
    public static void validateProfileCustomizationCache(ProfileCustomizationPO custom)
    {
        try{
            String desiredSavePath = ResourceMonitor.getResourcePath().toAbsolutePath()+ PROFILE_RELATIVE_PATH + custom.getPlayer_id()  +".jpg";
            File saveFilePath = new File(desiredSavePath);
            if (saveFilePath.exists()) {
                return;
            }
            logger.info("尝试重新获取图片缓存: {}", custom.getOriginal_url());
            AssertDownloadUtil.downloadResourceQueue(custom.getOriginal_url(), desiredSavePath);
            CommonTool.cropAndResize(desiredSavePath,1900,1000);
        }
        catch (Exception e) {
            throw new RuntimeException("尝试重新获取图片缓存失败: " + e.getMessage());
        }
    }
    private void insertProfileCustomizeToTable(CustomizationParameter params) throws IOException
    {
        ProfileCustomizationPO profile = new ProfileCustomizationPO();
        profile.setPlayer_id(params.getPlayerId());
        profile.setPlayer_name(params.getPlayerName());
        profile.setQq_code(params.getQqCode());
        profile.setOriginal_url(params.getTargetUrl());
        profile.setVerified(0);
        profile.setPreferred_type(0);
        profile.setLast_updated(LocalDateTime.now());
        profile.setHue(CommonTool.getDominantHueColorThief(new File(ResourceMonitor.getResourcePath().toAbsolutePath()+ "/osuFiles/playerCustomization/profile/" + params.getPlayerId() +".jpg")));
        Optional.ofNullable(customizationMapper.selectById(params.getPlayerId()))
                .ifPresentOrElse(
                        custom -> updateProfileCustomizeToTable(custom,profile),
                        () -> customizationMapper.insert(profile)
                );
    }
    private void updateProfileCustomizeToTable(ProfileCustomizationPO custom, ProfileCustomizationPO newCustom) {
        logger.info("更新Profile客制化请求: {}", custom.getPlayer_id());
        newCustom.setId(custom.getId());
        customizationMapper.updateById(newCustom);
    }
}
