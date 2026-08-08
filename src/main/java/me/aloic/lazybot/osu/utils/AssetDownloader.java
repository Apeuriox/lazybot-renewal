package me.aloic.lazybot.osu.utils;

import jakarta.annotation.Resource;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.monitor.ResourceMonitor;
import me.aloic.lazybot.osu.dao.entity.dto.sayobot.SayoData;
import me.aloic.lazybot.util.DataExtractor;
import me.aloic.lazybot.util.URLBuildUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;


//component version of AssetDownloadUtil for dependency injection
@Component
public class AssetDownloader
{
    private static final Logger logger = LoggerFactory.getLogger(AssetDownloader.class);

    @Resource
    private DataExtractor dataExtractor;

    /**
     * 从Sayobot下载谱面背景.
     * @return true表示下载成功且文件有效, false表示需要fallback
     */
    private boolean downloadBeatmapBackgroundFromSayobot(int sid)
    {
        String desiredLocalPath = ResourceMonitor.getResourcePath().toAbsolutePath() + "/osuFiles/mapBG/" + sid + ".jpg";
        File saveFilePath = new File(desiredLocalPath);
        if (saveFilePath.exists() && saveFilePath.length() > 0) {
            return true;
        }
        // remove 0 bytes file
        if (saveFilePath.exists()) {
            saveFilePath.delete();
        }
        try {
            SayoData sayoData = dataExtractor.extractSayobotBeatmapSet(sid);
            if (sayoData.getBid_data() == null || sayoData.getBid_data().isEmpty()) {
                logger.warn("Sayobot beatmapSet {} 无 bid_data, 回退到官方", sid);
                return false;
            }
            if (sayoData.getBid_data().getFirst().getBg() == null || sayoData.getBid_data().getFirst().getBg().isEmpty()) {
                logger.warn("Sayobot beatmapSet {} 无 BG 数据, 回退到官方", sid);
                return false;
            }
            String targetUrl = URLBuildUtil.buildURLOfSayobotMapBG(sid, sayoData.getBid_data().getFirst().getBg());
            return AssetDownloadUtil.backgroundDownload(desiredLocalPath, targetUrl, false);
        } catch (Exception e) {
            logger.warn("Sayobot背景下载失败 (sid={}): {}, 回退到官方", sid, e.getMessage());
            if (saveFilePath.exists()) {
                saveFilePath.delete();
            }
            return false;
        }
    }

    public String downloadBeatmapBackgroundFromSayobotByBid(int bid)
    {
        try {
            SayoData sayoData = dataExtractor.extractSayobotBeatmapSetByBid(bid);
            int sid = sayoData.getSid();
            String desiredLocalPath = ResourceMonitor.getResourcePath().toAbsolutePath() + "/osuFiles/mapBG/" + sid + ".jpg";
            File saveFilePath = new File(desiredLocalPath);
            if (saveFilePath.exists() && saveFilePath.length() > 0) {
                return desiredLocalPath;
            }
            if (sayoData.getBid_data() == null || sayoData.getBid_data().isEmpty()) {
                throw new LazybotRuntimeException("Sayobot无此谱面数据");
            }
            String targetUrl = URLBuildUtil.buildURLOfSayobotMapBG(sid, sayoData.getBid_data().getFirst().getBg());
            if (!AssetDownloadUtil.backgroundDownload(desiredLocalPath, targetUrl, false)) {
                throw new LazybotRuntimeException("Sayobot背景下载失败");
            }
            return desiredLocalPath;
        } catch (LazybotRuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new LazybotRuntimeException("Sayobot背景下载失败: " + e.getMessage());
        }
    }

    public String beatmapBackgroundAbsolutePath(Integer sid)
    {
        String desiredLocalPath = ResourceMonitor.getResourcePath().toAbsolutePath() + "/osuFiles/mapBG/" + sid + ".jpg";
        if (downloadBeatmapBackgroundFromSayobot(sid)) {
            File f = new File(desiredLocalPath);
            if (f.exists() && f.length() > 0) {
                return f.getAbsolutePath();
            }
        }
        return AssetDownloadUtil.backgroundAbsolutePath(sid);
    }

}
