package me.aloic.lazybot.osu.service.ServiceImpl;

import jakarta.annotation.Resource;
import me.aloic.lazybot.osu.dao.entity.po.TipsPO;
import me.aloic.lazybot.osu.dao.mapper.TipsMapper;
import me.aloic.lazybot.osu.service.FunService;
import me.aloic.lazybot.parameter.TipsParameter;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FunServiceImpl implements FunService
{
    @Resource
    private TipsMapper tipsMapper;
    @Override
    public String tips(TipsParameter parameter)
    {
        if (parameter.getId() == null || parameter.getId() == 0) {
            return Optional.ofNullable(tipsMapper.selectRandom())
                    .map(TipsPO::getContent)
                    .orElseThrow(() -> new RuntimeException("数据库查询出错"));
        }
        else {
          return Optional.ofNullable(tipsMapper.selectById(parameter.getId()))
                  .map(TipsPO::getContent)
                  .orElseThrow(() -> new RuntimeException("要么你参数输入错误，要么这个ID对应的tip不存在"));
        }
    }
}
