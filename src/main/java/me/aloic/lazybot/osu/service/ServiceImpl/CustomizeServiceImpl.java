package me.aloic.lazybot.osu.service.ServiceImpl;

import jakarta.annotation.Resource;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.monitor.ResourceMonitor;
import me.aloic.lazybot.osu.dao.entity.po.ProfileCustomizationPO;
import me.aloic.lazybot.osu.dao.mapper.CustomizationMapper;
import me.aloic.lazybot.osu.service.CustomizeService;
import me.aloic.lazybot.osu.theme.preset.ProfileTheme;
import me.aloic.lazybot.osu.utils.AssertDownloadUtil;
import me.aloic.lazybot.parameter.CustomizationParameter;
import me.aloic.lazybot.util.CommonTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CustomizeServiceImpl implements CustomizeService
{
    private static final String PROFILE_RELATIVE_PATH;

    @Resource
    private CustomizationMapper customizationMapper;

    private static final Logger logger = LoggerFactory.getLogger(CustomizeServiceImpl.class);

    static{
        PROFILE_RELATIVE_PATH = "\\osuFiles\\playerCustomization\\profile\\";
    }
    @Override
    public String customize(CustomizationParameter params)
    {
        if(params.getType().toLowerCase().trim().equals("profilebg")) {
            try {
                logger.info("开始处理Profile BG更改请求: {}", params);
                if(!(params.getTargetUrl().startsWith("http://") || params.getTargetUrl().startsWith("https://")))
                    throw new LazybotRuntimeException("超链接协议无效");
                profileBackgroundCustomize(params);
                insertProfileCustomizeToTable(params);
                return "已提交对"+params.getPlayerId()+"的背景图片修改请求，请等待验证";
            }
            catch (Exception e) {
                throw new LazybotRuntimeException("创建Profile客制化请求失败: " + e.getMessage());
            }
        }
        else if(params.getType().toLowerCase().trim().equals("profiletheme")) {
            try {
                logger.info("开始处理Profile Theme更改请求: {}", params);
                profileThemeUpdate(params);
                return "成功修改";
            }
            catch (Exception e) {
                throw new LazybotRuntimeException("处理Profile Theme更改请求失败: " + e.getMessage());
            }
        }
        else {
            throw new LazybotRuntimeException("未知的客制化类型: " + params.getType());
        }
    }
    private static void profileBackgroundCustomize(CustomizationParameter params)
    {
        try{
            String desiredSavePath = ResourceMonitor.getResourcePath().toAbsolutePath()+ PROFILE_RELATIVE_PATH + params.getPlayerId()  +".jpg";
            AssertDownloadUtil.downloadResourceQueue(params.getTargetUrl(), desiredSavePath);
            CommonTool.cropAndResize(desiredSavePath,1900,1000);
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            throw new LazybotRuntimeException("指定图片链接无法下载");
        }
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
            throw new LazybotRuntimeException("尝试重新获取图片缓存失败: " + e.getMessage());
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
    private void profileThemeUpdate(CustomizationParameter params) {
        Integer type= ProfileTheme.getTypeInt(params.getOtherParams());
        Optional.ofNullable(customizationMapper.selectById(params.getPlayerId()))
                .ifPresentOrElse(
                        custom -> customizationMapper.updatePreferredType(type,params.getPlayerId()),
                        this::throwIfNoCustomizationFound
                );
    }
    private void throwIfNoCustomizationFound() {
        throw new LazybotRuntimeException("未找到该用户的客制化请求，由于默认背景的关系，需要用户首先提交背景修改申请后再更改默认版本，可以单独做但是我现在懒了，不留点坑以后写什么呢");
    }
}
