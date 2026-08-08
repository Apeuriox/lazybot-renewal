package me.aloic.lazybot.graphics;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import me.aloic.lazybot.osu.dao.entity.dto.lazybot.RenderRequest;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import org.springframework.stereotype.Component;

@Component
public class TemplateRenderer
{
    private static final String TAKUMI_BASE_URL = "http://localhost:9090/api/render";

    public byte[] renderScore(ScoreVO scoreVO, int version)
    {
        String body = JSON.toJSONString(new RenderRequest<>(scoreVO, 1920, 1080),
                JSONWriter.Feature.WriteNonStringKeyAsString);
        return HttpUtil.createPost(TAKUMI_BASE_URL + "/score?version=" + version)
                .body(body)
                .contentType("application/json")
                .execute()
                .bodyBytes();
    }
}
