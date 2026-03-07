package me.aloic.lazybot.osu.service.ServiceImpl;

import jakarta.annotation.Resource;
import me.aloic.lazybot.entity.SongGuessWithTime;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.monitor.ResourceMonitor;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.osu.dao.entity.dto.lazybot.LazybotSongGuessData;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.dao.entity.po.TipsPO;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreIf;
import me.aloic.lazybot.osu.dao.mapper.TipsMapper;
import me.aloic.lazybot.osu.enums.OsuMod;
import me.aloic.lazybot.osu.service.FunService;
import me.aloic.lazybot.osu.utils.AssetDownloader;
import me.aloic.lazybot.parameter.DeviationFittingParameter;
import me.aloic.lazybot.parameter.GeneralParameter;
import me.aloic.lazybot.parameter.TipsParameter;
import me.aloic.lazybot.parameter.WhatIfParameter;
import me.aloic.lazybot.util.CommonTool;
import me.aloic.lazybot.util.DataExtractor;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.Stream;


@Service
public class FunServiceImpl implements FunService
{
    @Resource
    private TipsMapper tipsMapper;
    private static final Logger logger = LoggerFactory.getLogger(FunServiceImpl.class);
    @Value("${lazybot.command.whatif_calc_max_count}")
    private Integer MAX_CALC;
    @Resource
    private DataExtractor dataExtractor;
    @Resource
    private AssetDownloader assetDownloader;

    private static final Integer MAX_RETRIES = 3;



    @Override
    public String tips(TipsParameter parameter)
    {
        if (parameter.getId() == null || parameter.getId() == 0) {
            return Optional.ofNullable(tipsMapper.selectRandom())
                    .map(TipsPO::builderContent)
                    .orElseThrow(() -> new LazybotRuntimeException("数据库查询出错"));
        }
        else {
          return Optional.ofNullable(tipsMapper.selectById(parameter.getId()))
                  .map(TipsPO::builderContent)
                  .orElseThrow(() -> new LazybotRuntimeException("要么你参数输入错误，要么这个ID对应的tip不存在"));
        }
    }
    @Override
    public Path modInfo(GeneralParameter parameter)
    {
        if (parameter.getPlayerName() == null || parameter.getPlayerName().length() < 2) throw new LazybotRuntimeException("参数输入错误或为空");
        else {
            try{
                return ResourceMonitor.getResourcePath().resolve("static/modifier/"+ OsuMod.findAcronym(parameter.getPlayerName()) +".png");
            }
            catch (Exception e){
                logger.warn(e.getMessage());
                throw new LazybotRuntimeException("路径处理时出错");
            }
        }
    }


    @Override
    public String nameGuessGroupRandomName(List<Long> userIds)
    {
        List<AccessTokenPO> users = dataExtractor.extractPlayerInfoByUserIdBatch(userIds);
        if(CollectionUtils.isEmpty(users)) {
            throw new LazybotRuntimeException("当前群聊还没有人绑定Lazybot呢....");
        }
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                return dataExtractor.extractPlayerInfoDTO(users.get(new Random().nextInt(users.size())).getPlayer_id(),"osu").getUsername();
            }
            catch (Exception e) {
                logger.error("NameGuess获取用户名请求在尝试 {} 次后失败: {}", attempt, e.getMessage());
                if (attempt == MAX_RETRIES) {
                    throw new LazybotRuntimeException("NameGuess获取用户名请求在尝试 " + MAX_RETRIES + " 次后失败: " + e.getMessage(), e);
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    throw new LazybotRuntimeException("NameGuess请求线程中断", interrupted);
                }
            }
        }
        throw new LazybotRuntimeException("NameGuess获取用户名请求在尝试 " + MAX_RETRIES + " 次后失败，请稍后重试");

    }

    @Override
    public String whatIfIGotSomePP(WhatIfParameter params)
    {
        DecimalFormat df = new DecimalFormat("#.00");
        List<ScoreLazerDTO> scoreDTOList=dataExtractor.extractUserBestScoreList(
                String.valueOf(params.getPlayerId()),
                100,0, params.getMode());
        if (scoreDTOList.size() < 110) {
            scoreDTOList.addAll(dataExtractor.extractUserBestScoreList(
                    String.valueOf(params.getPlayerId()),
                    100,101,params.getMode()));
        }
        List<ScoreIf> existingScores = scoreDTOList.stream()
                .map(score -> new ScoreIf(score.getPp()))
                .toList();

        Double originalTotalPp = totalPpCalc(existingScores);
        PlayerInfoDTO playerInfo = dataExtractor.extractPlayerInfoDTO(params.getPlayerId(), params.getMode());
        Double bonusPp;
        try{
            bonusPp=playerInfo.getStatistics().getPp()-originalTotalPp;
        } catch (Exception e){
            throw new LazybotRuntimeException("获取用户pp错误");
        }
        Integer originalRank = Optional.ofNullable(playerInfo.getRank_history().getData()[playerInfo.getRank_history().getData().length-1]).orElse(-1);

        List<ScoreIf> fictionalScores = params.getInsertionMap().entrySet().stream()
                .flatMap(entry -> IntStream.range(0, entry.getValue())
                        .mapToObj(i -> new ScoreIf(entry.getKey())))
                .toList();
        List<ScoreIf> finalScores = Stream.concat(fictionalScores.stream(), existingScores.stream())
                .sorted(Comparator.comparing(ScoreIf::getPp).reversed())
                .limit(MAX_CALC)
                .toList();
        Double totalPp = totalPpCalc(finalScores);
        Integer rankFictional;
        try{
            rankFictional = dataExtractor.extractRankByPP(params.getMode(),totalPp + bonusPp);
        }
        catch (Exception e)
        {
            logger.error("获取whatIf 新pp rank时出错:{}", e.getMessage());
            throw new LazybotRuntimeException("获取whatIf 新pp rank时出错");
        }
        String rankDifference = originalRank-rankFictional>0?"+"+(originalRank-rankFictional):" - ";
        StringBuilder result= new StringBuilder("[Lazybot] " + playerInfo.getUsername() + "的pp变化情况：\n")
                .append("原pp: ").append(df.format(originalTotalPp+bonusPp)).append("\n")
                .append("现pp: ").append(df.format(totalPp+bonusPp))
                .append(" (+").append(df.format(totalPp-originalTotalPp)).append(") ").append("\n")
                .append("原Rank: #").append(originalRank).append("\n")
                .append("现Rank: #").append(rankFictional)
                .append(" (").append(rankDifference).append(") ");
        return result.toString();
    }

    @Override
    public String accuracyUsingNormalDistribution(DeviationFittingParameter params)
    {
        StringBuilder result = new StringBuilder();
        double accuracy = calculateEstimatedAccuracy(params.getOverallDifficulty(), params.getTargetUnstableRate());

        double accuracyValue= Math.pow(1.52163, params.getOverallDifficulty()) * Math.pow(accuracy, 24) * 2.83;
        double accuracyValueOf2000 = scaleAccuracyPerformanceWithNote(accuracyValue, 2000);
        double accuracyValueOf1000 = scaleAccuracyPerformanceWithNote(accuracyValue, 1000);
        double accuracyValueOf500 = scaleAccuracyPerformanceWithNote(accuracyValue, 500);
        result.append("[Lazybot] 在OD ").append(CommonTool.toString(params.getOverallDifficulty()))
                .append("下,\nUR ")
                .append(CommonTool.toString(params.getTargetUnstableRate()))
                .append("的准确率理论为:\n")
                .append(CommonTool.toString(accuracy*100D)).append("%\n")
                .append("\n在2000个note下的acc pp值为: ").append(CommonTool.toString(accuracyValueOf2000))
                .append("\n在1000个note下的acc pp值为: ").append(CommonTool.toString(accuracyValueOf1000))
                .append("\n在500个note下的acc pp值为: ").append(CommonTool.toString(accuracyValueOf500));

        return result.toString();
    }
    public static double calculateEstimatedAccuracy(double od, double ur)
    {
        double hitwindowOf300 = 80D - 6D * od;
        double hitwindowOf100 = 140D - 8D * od;
        double hitwindowOf50 = 200D - 10D *od;

        double d = ur / 10d;

        double rateOf300 = CommonTool.erf(hitwindowOf300 / (d * Math.sqrt(2)));
        double rateOf100 = CommonTool.erf(hitwindowOf100 / (d * Math.sqrt(2))) - rateOf300;
        double rateOf50 = CommonTool.erf(hitwindowOf50 / (d * Math.sqrt(2))) - rateOf300 - rateOf100;

        return rateOf300 + rateOf100 / 3 + rateOf50 / 6;
    }

    private static double scaleAccuracyPerformanceWithNote(double initialAccuracy, int noteCount)
    {
        return initialAccuracy * Math.min(1.15, Math.pow(noteCount / 1000.0, 0.3));
    }



    @Override
    public LazybotSongGuessData songGuessImage(GeneralParameter params)
    {
        LazybotSongGuessData result = new LazybotSongGuessData();
        int index = new Random().nextInt(200);
        List<ScoreLazerDTO> scoreDTO = dataExtractor.extractUserBestScoreList(
                String.valueOf(params.getPlayerId()),
                index,
                params.getMode());
        if (scoreDTO==null || scoreDTO.isEmpty()) {
            throw new LazybotRuntimeException("请重试，查询的用户bp未满200");
        }
        ScoreLazerDTO score = scoreDTO.getFirst();
        result.setMeta(new SongGuessWithTime(
                score.getBeatmapset().getTitle(),
                score.getBeatmapset().getCreator(),
                score.getBeatmapset().getArtist(),
                score.getBeatmap_id(),
                score.getBeatmap().getBeatmapset_id()));
        try{
            String urlOfBG =  assetDownloader.beatmapBackgroundAbsolutePath(score.getBeatmap().getBeatmapset_id());
            int resize = new Random().nextInt(5) + 1;
            BufferedImage original = cropImage(ImageIO.read(new File(urlOfBG)),resize);
            result.setResizeLevel(resize);
            result.setImg(toByteArray(original,"jpg"));
            return result;
        }
        catch (Exception e){
            logger.error("获取歌曲图片时出错:{}", e.getMessage());
            throw new LazybotRuntimeException("获取歌曲图片时出错");
        }
    }


    private static BufferedImage cropImage(BufferedImage src, int resize) {
        int originalWidth = src.getWidth();
        int originalHeight = src.getHeight();
        int cropWidth = originalWidth / resize;
        int cropHeight = originalHeight / resize;

        Random rand = new Random();
        int maxX = originalWidth - cropWidth;
        int maxY = originalHeight - cropHeight;
        int x = rand.nextInt(maxX + 1);
        int y = rand.nextInt(maxY + 1);

        return src.getSubimage(x, y, cropWidth, cropHeight);
    }

    private static byte[] toByteArray(BufferedImage image, String format) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, format, baos);
        return baos.toByteArray();
    }





    private Double totalPpCalc(List<ScoreIf> scoreList)
    {
        return IntStream.range(0, scoreList.size())
                .mapToDouble(i -> Math.pow(0.95, i) * scoreList.get(i).getPp())
                .sum();


    }
}
