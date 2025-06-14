package me.aloic.lazybot.osu.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.monitor.ResourceMonitor;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import me.aloic.lazybot.util.ContentUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.RuntimeMBeanException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.*;

public class AssertDownloadUtil
{
    private static final Logger logger = LoggerFactory.getLogger(AssertDownloadUtil.class);
    private static final int MAX_DOWNLOADS_PER_MINUTE;
    private static final long ONE_MINUTE_IN_MS;
    private static final DelayQueue<DownloadTask> delayQueue;
    private static final ExecutorService executor;
    private static final HttpClient httpClient;
    private static final int MAX_RETRIES;

    static{
        MAX_DOWNLOADS_PER_MINUTE=64;
        ONE_MINUTE_IN_MS=60*1000;
        delayQueue=new DelayQueue<>();
        executor=Executors.newScheduledThreadPool(6);
        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        MAX_RETRIES=3;
        initDownloadControl();

    }
    private static void initDownloadControl() {
        for (int i = 0; i < MAX_DOWNLOADS_PER_MINUTE; i++) {
            delayQueue.offer(new DownloadTask(1000));
        }
    }

    public static void downloadResourceQueue(String targetUrl, String desiredLocalPath) throws InterruptedException, ExecutionException {
        Future<Void> downloadFuture = executor.submit(() -> {
            try {
                delayQueue.take();
                fileDownloadJavaHttpClient(targetUrl, desiredLocalPath);
                delayQueue.offer(new DownloadTask(ONE_MINUTE_IN_MS));
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
                throw new LazybotRuntimeException("Download thread was interrupted: " +e.getMessage());
            }
            return null;
        });
        downloadFuture.get();
        logger.info("(QUEUE) Download completed for: {}", targetUrl);
    }
    private static void downloadResourceSmallQueue(String targetUrl, String desiredLocalPath) throws InterruptedException, ExecutionException {
        Future<Void> downloadFuture = executor.submit(() -> {
            try {
                delayQueue.take();
                fileDownloadJavaHttpClient(targetUrl, desiredLocalPath);
                delayQueue.offer(new DownloadTask(ONE_MINUTE_IN_MS));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        });
        downloadFuture.get();
        logger.info("Download completed for: {}", targetUrl);
    }
    public static boolean beatmapDownload(Integer bid,Boolean override)
    {
        String desiredLocalPath= ResourceMonitor.getResourcePath().toAbsolutePath()+ "/osuFiles/" + bid +".osu";
        File saveFilePath = new File(desiredLocalPath);
        if (saveFilePath.exists() && !override) {
            logger.info("地图.osu文件已存在: {}", saveFilePath.getAbsolutePath());
            return false;
        }
        String targetUrl= ContentUtil.BEATMAP_DOWNLOAD_URL+ "/" +bid;
        try{
            downloadResourceSmallQueue(targetUrl,desiredLocalPath);
        }
        catch (Exception e)
        {
            logger.error("地图下载失败: {}", e.getMessage());
        }

        return true;
    }
    public static Path backgroundDownload(Integer sid)
    {
        String desiredLocalPath= ResourceMonitor.getResourcePath().toAbsolutePath()+ "/osuFiles/mapBG/" + sid +".jpg";
        File saveFilePath = new File(desiredLocalPath);
        if (saveFilePath.exists()) {
            logger.info("地图背景文件已存在: {}", saveFilePath.getAbsolutePath());
            return Paths.get(desiredLocalPath);
        }
        String targetUrl = ContentUtil.BEATMAP_BG_BASE_URL+sid+"/covers/raw.jpg";
        try{
            downloadResourceQueue(targetUrl,desiredLocalPath);
        }
        catch (Exception e)
        {
            logger.error("地图背景下载失败: {}", e.getMessage());
        }
        return Paths.get(desiredLocalPath);
    }

    public static Path avatarDownload(String url,int playerId, boolean override)
    {
        return assertDownload(url,"playerAvatar", String.valueOf(playerId),"jpg",override);
    }
    public static Path bannerDownload(String url,int playerId, boolean override)
    {
        return assertDownload(url,"playerBanner", String.valueOf(playerId),"jpg",override);
    }
    public static Path assertDownload(String url,String subPath,String fileName,String fileExtension, boolean override)
    {
        String desiredLocalPath= ResourceMonitor.getResourcePath().toAbsolutePath()+ "/osuFiles/"+ subPath + "/" + fileName + "." + fileExtension;
        File saveFilePath = new File(desiredLocalPath);
        if (saveFilePath.exists()&&!override) {
            logger.info("该文件已存在: {}", saveFilePath.getAbsolutePath());
            return Paths.get(desiredLocalPath);
        }
        try{
            downloadResourceQueue(url,desiredLocalPath);
        }
        catch (Exception e)
        {
            logger.error("下载失败: {}", e.getMessage());
            throw new LazybotRuntimeException("[Lazybot] 下载线程出错: "+ e.getMessage());
        }
        return Paths.get(desiredLocalPath);
    }


    public static Path beatmapPath(Integer bid,Boolean override)
    {
        beatmapDownload(bid,override);
        return Paths.get(ResourceMonitor.getResourcePath().toAbsolutePath()+ "/osuFiles/" +bid +".osu");
    }
    public static Path beatmapPath(ScoreVO scoreVO, Boolean override)
    {
        beatmapDownload(scoreVO.getBeatmap().getBid(),override);
        return Paths.get(ResourceMonitor.getResourcePath().toAbsolutePath()+ "/osuFiles/" +scoreVO.getBeatmap().getBid() +".osu");
    }
    public static String svgAbsolutePath(Integer sid)
    {
        backgroundDownload(sid);
        return ResourceMonitor.getResourcePath().toAbsolutePath()+ "/osuFiles/mapBG/" + sid +".jpg";
    }
    public static String avatarAbsolutePath(ScoreVO scoreVO, int playerId, boolean override)
    {
        avatarDownload(scoreVO.getAvatarUrl(),playerId,override);
        return ResourceMonitor.getResourcePath().toAbsolutePath()+ "/osuFiles/playerAvatar/" + playerId +".jpg";
    }
    public static String avatarAbsolutePath(PlayerInfoDTO playerInfoDTO, boolean override)
    {
        avatarDownload(playerInfoDTO.getAvatar_url(), playerInfoDTO.getId(),override);
        return ResourceMonitor.getResourcePath().toAbsolutePath()+ "/osuFiles/playerAvatar/" + playerInfoDTO.getId() +".jpg";
    }

    public static String bannerAbsolutePath(PlayerInfoDTO playerInfoDTO, boolean override)
    {
        bannerDownload(playerInfoDTO.getCover_url(), playerInfoDTO.getId(),override);
        return ResourceMonitor.getResourcePath().toAbsolutePath()+ "/osuFiles/playerBanner/" + playerInfoDTO.getId() +".jpg";
    }
    private static void fileDownloadJavaHttpClient(String targetUrl, String desiredLocalPath) throws Exception {
        int attempt = 0;
        while (attempt < MAX_RETRIES) {
            try {
                attempt++;
                logger.info("尝试下载文件 (第 {} 次)： {} to {}", attempt, targetUrl, desiredLocalPath);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(targetUrl))
                        .timeout(Duration.ofSeconds(30))
                        .GET()
                        .build();
                Path path = Path.of(desiredLocalPath);
                HttpResponse<Path> response = httpClient.send(request, HttpResponse.BodyHandlers.ofFile(path));
                if (response.statusCode() == 200) {
                    logger.info("文件下载成功，保存路径：{}", desiredLocalPath);
                    return;
                } else {
                    throw new LazybotRuntimeException("HTTP 状态码：" + response.statusCode());
                }
            } catch (Exception e) {
                logger.warn("下载失败 (第 {} 次): {}", attempt, e.getMessage());
                if (attempt >= MAX_RETRIES) {
                    logger.error("重试三次后仍无法下载");
                    throw new LazybotRuntimeException("三次重试后仍下载失败: " + e.getMessage());
                }
                Thread.sleep(2000);
            }
        }
    }


    static class DownloadTask implements Delayed {
        private long delayTime;
        private long createdTime;

        public DownloadTask(long delayTime) {
            this.delayTime = delayTime;
            this.createdTime = System.currentTimeMillis();
        }

        @Override
        public long getDelay(TimeUnit unit) {
            long elapsed = System.currentTimeMillis() - createdTime;
            return unit.convert(delayTime - elapsed, TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            return Long.compare(this.createdTime, ((DownloadTask) o).createdTime);
        }
    }

}
