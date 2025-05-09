package me.aloic.lazybot.osu.service.ServiceImpl;

import jakarta.annotation.Resource;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.monitor.ResourceMonitor;
import me.aloic.lazybot.osu.dao.entity.po.TipsPO;
import me.aloic.lazybot.osu.dao.mapper.TipsMapper;
import me.aloic.lazybot.osu.enums.OsuMod;
import me.aloic.lazybot.osu.monitor.TokenMonitor;
import me.aloic.lazybot.osu.service.FunService;
import me.aloic.lazybot.parameter.GeneralParameter;
import me.aloic.lazybot.parameter.TipsParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Optional;


@Service
public class FunServiceImpl implements FunService
{
    @Resource
    private TipsMapper tipsMapper;
    private static final Logger logger = LoggerFactory.getLogger(FunServiceImpl.class);



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
}
