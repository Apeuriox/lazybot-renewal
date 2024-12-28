package me.aloic.lazybot.osu.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import me.aloic.lazybot.monitor.ResourceMonitor;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import me.aloic.lazybot.util.ContentUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.*;

public class AssertDownloadUtil
{
    private static final Logger logger = LoggerFactory.getLogger(AssertDownloadUtil.class);
    private static final int MAX_DOWNLOADS_PER_MINUTE;
    private static final long ONE_MINUTE_IN_MS;
    private static final DelayQueue<DownloadTask> delayQueue;
    private static final ExecutorService executor;

    static{
        MAX_DOWNLOADS_PER_MINUTE=64;
        ONE_MINUTE_IN_MS=60*1000;
        delayQueue=new DelayQueue<>();
        executor=Executors.newScheduledThreadPool(2);
        initDownloadControl();
    }
    private static void initDownloadControl() {
        for (int i = 0; i < MAX_DOWNLOADS_PER_MINUTE; i++) {
            delayQueue.offer(new DownloadTask(1000));
        }
    }
    private static void downloadResourceQueue(String targetUrl, String desiredLocalPath) throws InterruptedException, ExecutionException {
        Future<Void> downloadFuture = executor.submit(() -> {
            try {
                delayQueue.take();
                largeResourceDownload(targetUrl, desiredLocalPath);
                delayQueue.offer(new DownloadTask(ONE_MINUTE_IN_MS));
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException("Download thread was interrupted: " +e.getMessage());
            }
            return null;
        });
        downloadFuture.get();
        logger.info("Download completed for: {}", targetUrl);
    }
    private static void downloadResourceSmallQueue(String targetUrl, String desiredLocalPath) throws InterruptedException, ExecutionException {
        Future<Void> downloadFuture = executor.submit(() -> {
            try {
                delayQueue.take();
                resourceDownload(targetUrl, desiredLocalPath);
                delayQueue.offer(new DownloadTask(ONE_MINUTE_IN_MS));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        });
        downloadFuture.get();
        logger.info("Download completed for: {}", targetUrl);
    }
    public static boolean beatmapDownload(Integer bid)
    {
        String desiredLocalPath= ResourceMonitor.getResourcePath().toAbsolutePath()+ "/osuFiles/" + bid +".osu";
        File saveFilePath = new File(desiredLocalPath);
        if (saveFilePath.exists()) {
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
        String desiredLocalPath= ResourceMonitor.getResourcePath().toAbsolutePath()+ "/osuFiles/playerAvatar/" + playerId +".jpg";
        File saveFilePath = new File(desiredLocalPath);
        if (saveFilePath.exists()&&!override) {
            logger.info("玩家头像文件已存在: {}", saveFilePath.getAbsolutePath());
            return Paths.get(desiredLocalPath);
        }
        try{
            downloadResourceQueue(url,desiredLocalPath);
        }
        catch (Exception e)
        {
            logger.error("地图下载失败: {}", e.getMessage());
            throw new RuntimeException("下载线程出错: "+ e.getMessage());
        }
        return Paths.get(desiredLocalPath);
    }


    public static Path beatmapPath(Integer bid)
    {
        beatmapDownload(bid);
        return Paths.get(ResourceMonitor.getResourcePath().toAbsolutePath()+ "/osuFiles/" +bid +".osu");
    }
    public static Path beatmapPath(ScoreVO scoreVO)
    {
        beatmapDownload(scoreVO.getBeatmap().getBid());
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
    private static void resourceDownload(String targetUrl,String desiredLocalPath)
    {
        ReadableByteChannel rbc = null;
        FileOutputStream fos = null;
        try {
            logger.info("请求文件不存在，正在下载（小型文件）");
            URL ppySource = new URL(targetUrl);
            rbc = Channels.newChannel(ppySource.openStream());
            fos = new FileOutputStream(desiredLocalPath);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } catch (Exception e)
        {
            logger.error("下载小型文件时出错:{}", e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("下载时出错: " +e.getMessage() +" 如果出现此消息请重试 ");
        }
        finally{
            logger.info("下载完成（小型文件）: {}", desiredLocalPath);
            if(fos!=null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(rbc!=null){
                try {
                    rbc.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
    private static void largeResourceDownload(String targetUrl,String desiredLocalPath)
    {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            logger.info("请求文件不存在，正在下载");
            URL url = new URL(targetUrl);
            //这里没有使用 封装后的ResponseEntity 就是也是因为这里不适合一次性的拿到结果，放不下content,会造成内存溢出
            HttpURLConnection connection =(HttpURLConnection) url.openConnection();

            inputStream = new BufferedInputStream(connection.getInputStream());
            File file = new File(desiredLocalPath);
            if (file.exists()) {
                file.delete();
            }
            outputStream = Files.newOutputStream(file.toPath());
            byte[] buffer = new byte[1024 * 1024 * 3];// 2MB
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            connection.disconnect();
        }catch (Exception e){
            e.printStackTrace();
            logger.error("下载时出错:{}", e.getMessage());
            throw new RuntimeException("下载时出错:" +e.getMessage() +" 如果出现此消息请重试 ");

        }finally {
            logger.info("下载完成: {}", desiredLocalPath);
            IOUtils.closeQuietly(outputStream);
            IOUtils.closeQuietly(inputStream);
        }
    }
    private static long largeResourceDownloadHutools(String targetUrl,String desiredLocalPath)
    {
       return HttpUtil.downloadFile(targetUrl, FileUtil.file(desiredLocalPath));
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
