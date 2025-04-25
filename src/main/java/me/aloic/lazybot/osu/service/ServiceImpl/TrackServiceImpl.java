package me.aloic.lazybot.osu.service.ServiceImpl;

import me.aloic.lazybot.entity.DiamondShape;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.BeatmapDTO;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.BeatmapsetDTO;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.osu.dao.entity.dto.osuTrack.BestPlay;
import me.aloic.lazybot.osu.dao.entity.vo.HitScoreVO;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreSequence;
import me.aloic.lazybot.osu.service.TrackService;
import me.aloic.lazybot.osu.utils.*;
import me.aloic.lazybot.parameter.GeneralParameter;
import me.aloic.lazybot.parameter.TopScoresParameter;
import me.aloic.lazybot.util.DataObjectExtractor;
import me.aloic.lazybot.util.TransformerUtil;
import me.aloic.lazybot.util.VirtualThreadExecutorHolder;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TrackServiceImpl implements TrackService
{
    private static final Logger logger = LoggerFactory.getLogger(TrackServiceImpl.class);
    private static final Map<String, Color> rankColorMap;
    private static final Map<String,Shape> rankShapeMap;

    static{
        rankColorMap = Map.of(
                "A rank", new Color(149, 201, 134, 191),
                "B rank", new Color(145, 153, 203, 191),
                "C rank", new Color(197, 147, 211, 191),
                "D rank", new Color(242, 131, 131, 191),
                "S rank", new Color(242, 218, 141, 191),
                "X rank", new Color(255, 64, 128, 191),
                "SH rank", new Color(148, 160, 158, 191),
                "XH rank", new Color(167, 178, 179, 191)
        );
        rankShapeMap = Map.of(
                "A rank", new java.awt.geom.Ellipse2D.Double(-8, -8, 16, 16),
                "B rank", new Polygon(new int[]{8, 0, 16},
                        new int[]{14, 0, 0},
                        3),
                "C rank", new Polygon(new int[]{8, 0, 16},
                        new int[]{0, 14, 14},
                        3),
                "D rank", new DiamondShape(18,18),
                "S rank", new java.awt.geom.Rectangle2D.Double(-8, -8, 16, 16),
                "X rank", new DiamondShape(18,18),
                "SH rank", new java.awt.geom.Rectangle2D.Double(-8, -8, 16, 16),
                "XH rank", new DiamondShape(18,18)
        );
    }
    @Override
    public byte[] ppTimeMap(GeneralParameter params) throws Exception
    {
        java.util.List<HitScoreVO> hitScoreVOs= DataObjectExtractor.extractOsuTrackHitScoreList(params.getPlayerId(), params.getMode());
        logger.info("ppMap转换后对象数量：{}", hitScoreVOs.size());

        ZonedDateTime dateTime1 = ZonedDateTime.parse(hitScoreVOs.getFirst().getScoreTimeJSON());
        ZonedDateTime dateTime2 = ZonedDateTime.parse(hitScoreVOs.getLast().getScoreTimeJSON());
        int targetWidth;
        long month= ChronoUnit.MONTHS.between(dateTime1, dateTime2);
        if(month>12) {
            targetWidth= (int)(51.5 * month);
        }
        else {
            targetWidth= (int)(120 * month);
        }
        targetWidth=Math.max(targetWidth,1200);

        List<TimeSeries> seriesList= setupTimeSeries(hitScoreVOs);
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        for (TimeSeries timeSeries : seriesList) {
            dataset.addSeries(timeSeries);
        }
        logger.info("Dataset type count：{}", seriesList.size());
        JFreeChart chart = ChartFactory.createScatterPlot(
                "Best Performance Time-pp Scatter Chart of "+params.getPlayerName() +" , Mode: " + params.getMode(),    // 图表标题
                "Achieved Time",                 // X轴标签
                "PP Values",         // Y轴标签
                dataset,                // 数据集
                PlotOrientation.VERTICAL,// 布局方向
                true,                   // 是否显示图例
                true,                   // 是否生成工具提示
                false                   // 是否生成URL链接
        );

        chart.setBackgroundPaint(new Color(243, 243, 243));
        XYPlot plot = (XYPlot) chart.getPlot();
        DateAxis dateAxis = new DateAxis();
        dateAxis.setDateFormatOverride(new SimpleDateFormat("yyyy-MM"));
        plot.setDomainAxis(dateAxis);
        chart.getLegend().setItemFont(new Font("Gilroy", Font.PLAIN, 20));
        chart.getTitle().setFont(new Font("Gilroy", Font.PLAIN, 35));
        plot.getRangeAxis().setTickLabelFont(new Font("Gilroy", Font.PLAIN, 15));
        plot.getDomainAxis().setTickLabelFont(new Font("Gilroy", Font.PLAIN, 15));
        plot.getRangeAxis().setLabelFont(new Font("Gilroy", Font.BOLD, 25));
        plot.getDomainAxis().setLabelFont(new Font("Gilroy", Font.BOLD, 25));
        plot.setBackgroundPaint(Color.white);
        plot.setDomainGridlinePaint(new Color(210, 210, 210));
        plot.setRangeGridlinePaint(Color.lightGray);

        Map<Integer,Color> seriesSeq= mappingRankToObject(
                seriesList,
                series -> (String) series.getKey(), // 提取键的逻辑
                rankColorMap
        );
        Map<Integer,Shape> seriesSeqShape=mappingRankToObject(
                seriesList,
                series -> (String) series.getKey(), // 提取键的逻辑
                rankShapeMap
        );
        for(int i=0;i<seriesSeq.size();i++) {
            plot.getRenderer().setSeriesPaint(i, seriesSeq.get(i));
            plot.getRenderer().setSeriesShape(i, seriesSeqShape.get(i));
        }
        //use if you want to export it into file
//        File file = new File("date_scatter_plot.png");
//        ChartUtils.saveChartAsPNG(file, chart, 3400, 1000);
        ByteArrayOutputStream bao= new ByteArrayOutputStream();
        ChartUtils.writeChartAsJPEG(bao,chart,targetWidth,1000);
        return bao.toByteArray();
    }




    private java.util.List<TimeSeries> setupTimeSeries(java.util.List<HitScoreVO> hitScores)
    {
        Map<String, TimeSeries> seriesMap = new HashMap<>();
        java.util.List<String> ranks = Arrays.asList("A", "B", "C", "D", "S", "X", "SH", "XH");
        ranks.forEach(rank -> seriesMap.put(rank, new TimeSeries(rank + " rank")));
        for (HitScoreVO hitScore : hitScores) {
            TimeSeries series = seriesMap.get(hitScore.getRank());
            if (series != null) {
                series.addOrUpdate(new Minute(hitScore.getAchievedTime()), hitScore.getPp());
            }
        }
        return seriesMap.values().stream()
                .filter(series -> !series.isEmpty())
                .collect(Collectors.toList());
    }

    private <T, K> Map<Integer, K> mappingRankToObject(List<T> items, Function<T, String> keyExtractor, Map<String, K> mapping)
    {
        Map<Integer, K> result = new HashMap<>();
        for (int i = 0; i < items.size(); i++) {
            String key = keyExtractor.apply(items.get(i));
            K value = mapping.get(key);
            if (value != null) {
                result.put(i, value);
            }
        }
        return result;
    }

    @Override
    public byte[] bestPlaysInGamemode(TopScoresParameter params) throws IOException
    {
        List<BestPlay> bestPlayListDistinct = new ArrayList<>(
                DataObjectExtractor.extractOsuTrackBestPlay(params.getLimit(),params.getRuleset().getValue()).stream()
                        .collect(Collectors.toMap(
                                BestPlay::getScoreKey,
                                singlePlay -> singlePlay,
                                (existing, replacement) -> existing
                        ))
                        .values());
        logger.info("过滤后数据长度为: {}",bestPlayListDistinct.size());
        List<CompletableFuture<ScoreLazerDTO>> futures = bestPlayListDistinct.stream()
                .map(bestPlay -> CompletableFuture.supplyAsync(() -> {
                    try {
                        List<ScoreLazerDTO> scoreList = DataObjectExtractor.extractBeatmapUserScoreAll(
                                params.getAccessToken(),
                                bestPlay.getBeatmap_id(),
                                bestPlay.getUser(),
                                params.getRuleset().getDescribe());

                        if (scoreList != null && !scoreList.isEmpty()) {
                            scoreList.sort(Comparator.comparing(ScoreLazerDTO::getPp).reversed());

                            BeatmapDTO beatmapDTO = DataObjectExtractor.extractBeatmap(
                                    params.getAccessToken(),
                                    String.valueOf(bestPlay.getBeatmap_id()),
                                    params.getRuleset().getDescribe());

                            ScoreLazerDTO topScore = scoreList.getFirst();
                            topScore.setBeatmap(beatmapDTO);
                            topScore.setBeatmapset(beatmapDTO.getBeatmapset());

                            topScore.setUser(DataObjectExtractor.extractPlayerInfo(
                                    params.getAccessToken(),
                                    bestPlay.getUser(),
                                    params.getRuleset().getDescribe()));
                            return topScore;
                        }
                    } catch (Exception e) {
                        logger.warn("获取单个BestPlay数据失败: {}", e.getMessage());
                    }
                    return null;
                }, VirtualThreadExecutorHolder.VIRTUAL_EXECUTOR))
                .toList();
        List<ScoreLazerDTO> listOfScores = futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(ScoreLazerDTO::getPp).reversed())
                .collect(Collectors.toList());

        logger.info("存在成绩长度为: {}",listOfScores.size());
        List<ScoreSequence> scoreSequences=TransformerUtil.scoreSequenceListTransform(listOfScores);
//                listOfScores).stream().
//                filter(scoreSequence -> scoreSequence.getDifferenceBetweenNextScore()>=0)
//                .toList();
        logger.info("最终过滤长度为: {}",scoreSequences.size());
        OsuToolsUtil.setUpImageStaticSequence(scoreSequences);
        return SVGRenderUtil.renderSVGDocumentToByteArray(SvgUtil.createScoreListDetailed(scoreSequences,"#f8bad4","Current Best Plays of osu! by PP Earned",1));
    }

}
