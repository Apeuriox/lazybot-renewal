package me.aloic.lazybot.osu.utils;

import jakarta.annotation.Resource;
import me.aloic.lazybot.monitor.ResourceMonitor;
import me.aloic.lazybot.osu.dao.entity.dto.sayobot.SayoData;
import me.aloic.lazybot.util.DataExtractor;
import me.aloic.lazybot.util.URLBuildUtil;
import org.springframework.stereotype.Component;

import java.io.File;


//component version of AssetDownloadUtil for dependency injection
@Component
public class AssetDownloader
{
    @Resource
    private DataExtractor dataExtractor;

    public void downloadBeatmapBackgroundFromSayobot(int sid)
    {
        String desiredLocalPath= ResourceMonitor.getResourcePath().toAbsolutePath()+ "/osuFiles/mapBG/" + sid +".jpg";
        File saveFilePath = new File(desiredLocalPath);
        if (saveFilePath.exists()) {
            return;
        }
        SayoData sayoData = dataExtractor.extractSayobotBeatmapSet(sid);
        String targetUrl = URLBuildUtil.buildURLOfSayobotMapBG(sid,sayoData.getBid_data().getFirst().getBg());
        AssetDownloadUtil.backgroundDownload(desiredLocalPath,targetUrl,false);
    }
    public String downloadBeatmapBackgroundFromSayobotByBid(int bid)
    {
        SayoData sayoData = dataExtractor.extractSayobotBeatmapSetByBid(bid);
        int sid = sayoData.getSid();
        String desiredLocalPath= ResourceMonitor.getResourcePath().toAbsolutePath()+ "/osuFiles/mapBG/" + sid +".jpg";
        File saveFilePath = new File(desiredLocalPath);
        if (saveFilePath.exists()) {
            return desiredLocalPath;
        }
        String targetUrl = URLBuildUtil.buildURLOfSayobotMapBG(sid,sayoData.getBid_data().getFirst().getBg());
        AssetDownloadUtil.backgroundDownload(desiredLocalPath,targetUrl,false);
        return desiredLocalPath;
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
