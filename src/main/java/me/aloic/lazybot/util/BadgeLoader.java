package me.aloic.lazybot.util;

import lombok.extern.slf4j.Slf4j;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.monitor.ResourceMonitor;
import me.aloic.lazybot.osu.utils.AssertDownloadUtil;
import me.aloic.lazybot.parameter.BadgeImageParameter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class BadgeLoader
{

    private static final String BADGE_RELATIVE_PATH;

    static{
        BADGE_RELATIVE_PATH = "static/badge/";
    }

    public static byte[] loadBadgeImage(Integer id) throws IOException
    {
        Path filePath = ResourceMonitor.getResourcePath().toAbsolutePath().resolve(BADGE_RELATIVE_PATH + id + ".png");
        File image = filePath.toFile();
        if (image.exists()) {
            return Files.readAllBytes(Paths.get(filePath.toUri()));
        }
        log.warn("Image of Badge {} not found", id);
        return null;
    }
    public static String loadBadgeImagePath(Integer id)
    {
        Path filePath = ResourceMonitor.getResourcePath().toAbsolutePath().resolve(BADGE_RELATIVE_PATH + id + ".png");
        File image = filePath.toFile();
        if (image.exists()) {
            return filePath.toAbsolutePath().toString();
        }
        log.warn("Image of Badge Path {} not found", id);
        return null;
    }


    public static void badgeImageCacheDownload(BadgeImageParameter params)
    {
        badgeImageCacheDownload(params.getBadgeId(),params.getTargetUrl());
    }
    public static void badgeImageCacheDownload(Integer badgeId, String url)
    {
        if (url == null) return;
        try{
            String desiredSavePath = ResourceMonitor.getResourcePath().toAbsolutePath()+ BADGE_RELATIVE_PATH + badgeId  +".png";
            AssertDownloadUtil.downloadResourceQueue(url, desiredSavePath);
            CommonTool.cropAndResize(desiredSavePath,800,400);
        }
        catch (Exception e) {
            log.error(e.getMessage());
            throw new LazybotRuntimeException("指定Badge图片链接无法下载");
        }
    }
}
