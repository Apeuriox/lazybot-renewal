package me.aloic.lazybot.osu.service.ServiceImpl;

import jakarta.annotation.Resource;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.monitor.ResourceMonitor;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.po.TipsPO;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreIf;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import me.aloic.lazybot.osu.dao.mapper.TipsMapper;
import me.aloic.lazybot.osu.enums.OsuMod;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.service.FunService;
import me.aloic.lazybot.osu.utils.OsuToolsUtil;
import me.aloic.lazybot.parameter.GeneralParameter;
import me.aloic.lazybot.parameter.TipsParameter;
import me.aloic.lazybot.parameter.WhatIfParameter;
import me.aloic.lazybot.util.ApiRequestStarter;
import me.aloic.lazybot.util.DataObjectExtractor;
import me.aloic.lazybot.util.URLBuildUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
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
                throw new LazybotRuntimeException("[mod] 路径处理时出错");
            }
        }
    }
    @Override
    public String whatIfIGotSomePP(WhatIfParameter params)
    {
        DecimalFormat df = new DecimalFormat("#.00");
        List<ScoreIf> existingScores = DataObjectExtractor.extractUserBestScoreList(
                params.getAccessToken(),
                String.valueOf(params.getPlayerId()),
                200,0,
                params.getMode()).stream()
                .map(score -> new ScoreIf(score.getPp()))
                .toList();
        Double originalTotalPp = totalPpCalc(existingScores);
        PlayerInfoDTO playerInfo = OsuToolsUtil.getUserInfoByUsername(params.getPlayerName(), params.getAccessToken(), params.getMode());
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
        Integer rankFictional =ApiRequestStarter.excuteInteger(URLBuildUtil.buildURLOfPpRank(OsuMode.getMode(params.getMode()).getValue(), (int) Math.round(totalPp+bonusPp)));
        String rankDifference = originalRank-rankFictional>0?"+"+(originalRank-rankFictional):" - ";
        StringBuilder result= new StringBuilder(playerInfo.getUsername() + "的pp变化情况：\n")
                .append("原pp: ").append(df.format(originalTotalPp+bonusPp)).append("\n")
                .append("现pp: ").append(df.format(totalPp+bonusPp))
                .append(" (+").append(df.format(totalPp-originalTotalPp)).append(") ").append("\n")
                .append("原Rank: #").append(originalRank).append("\n")
                .append("现Rank: #").append(rankFictional)
                .append(" (").append(rankDifference).append(") ");
        return result.toString();
    }
    private Double totalPpCalc(List<ScoreIf> scoreList)
    {
        return IntStream.range(0, scoreList.size())
                .mapToDouble(i -> Math.pow(0.95, i) * scoreList.get(i).getPp())
                .sum();


    }
}
