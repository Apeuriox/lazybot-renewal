package me.aloic.lazybot.osu.utils;

import jakarta.annotation.Resource;
import me.aloic.lazybot.monitor.ResourceMonitor;
import me.aloic.lazybot.osu.dao.entity.dto.sayobot.SayoData;
import me.aloic.lazybot.util.DataExtractor;
import me.aloic.lazybot.util.URLBuildUtil;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class AssetDownloader
{
    @Resource
    private DataExtractor dataExtractor;;

    public void downloadBeatmapBackgroundFromSayobot(int sid)
    {
        String desiredLocalPath= ResourceMonitor.getResourcePath().toAbsolutePath()+ "/osuFiles/mapBG/" + sid +".jpg";
        File saveFilePath = new File(desiredLocalPath);
        if (saveFilePath.exists()) {
            return;
        }
        SayoData sayoData = dataExtractor.extractSayobotBeatmapSet(sid);
        String targetUrl = URLBuildUtil.buildURLOfSayobotMapBG(sid,sayoData.getBid_data().getFirst().getBg());
        AssetDownloadUtil.backgroundDownload(desiredLocalPath,targetUrl);
    }

    public String beatmapBackgroundAbsolutePath(Integer sid)
    {
        String desiredLocalPath= ResourceMonitor.getResourcePath().toAbsolutePath()+ "/osuFiles/mapBG/" + sid +".jpg";
        downloadBeatmapBackgroundFromSayobot(sid);
        File saveFilePath = new File(desiredLocalPath);
        if (saveFilePath.exists()) {
            return saveFilePath.getAbsolutePath();
        }
        return AssetDownloadUtil.svgAbsolutePath(sid);
    }

}
