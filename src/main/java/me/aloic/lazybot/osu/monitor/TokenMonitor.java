package me.aloic.lazybot.osu.monitor;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.alibaba.fastjson.TypeReference;
import jakarta.annotation.Resource;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.dto.lazybot.LazybotWebResult;
import me.aloic.lazybot.osu.dao.entity.dto.oauth.AccessTokenDTO;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.dao.entity.po.UserTokenPO;
import me.aloic.lazybot.osu.dao.mapper.DiscordTokenMapper;
import me.aloic.lazybot.osu.dao.mapper.TokenMapper;
import me.aloic.lazybot.util.URLBuildUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
/**
 * 定时刷新token任务，因为所有申请到的token均只有一天的有效期，所以需要在一天内刷新拿到新的token.
 * 使用定时任务，刷新token的同时获取玩家基本信息，每小时刷新一次。
 */
@Component
public class TokenMonitor
{
    @Value("${lazybot.client_id}")
    private Integer clientId;
    @Value("${lazybot.client_secret}")
    private String clientSecret;

    @Value("${lazybot.plus.client_id}")
    private Integer lazybotClientId;
    @Value("${lazybot.plus.client_secret}")
    private String lazybotClientSecret;
    @Resource
    private DiscordTokenMapper discordTokenMapper;
    @Resource
    private TokenMapper tokenMapper;



    private static String lazybotToken;

    private static final Logger logger = LoggerFactory.getLogger(TokenMonitor.class);

    @Scheduled(cron = "0 0 0/12 * * ? ")
    public void refreshClientToken()
    {
        try {
            logger.info("Getting Token for client");
            String url = "https://osu.ppy.sh/oauth/token";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("client_id", clientId);
            jsonObject.put("client_secret", clientSecret);
            jsonObject.put("grant_type", "client_credentials");
            jsonObject.put("scope", "public");
            Map<String, String > heads = new HashMap<>();
            heads.put("Accept", "application/json");
            heads.put("Content-Type", "application/json;charset=UTF-8");
            AccessTokenDTO tokenDTO = JSON.parseObject(HttpUtil.createPost(url).addHeaders(heads).body(jsonObject.toString()).execute().body(),
                    AccessTokenDTO.class);
            UserTokenPO tokenPO = new UserTokenPO(tokenDTO.getRefresh_token(), tokenDTO.getAccess_token());
            tokenPO.setPlayer_name("CLIENT");
            tokenPO.setPlayer_id(0);
            tokenPO.setDiscord_code(0L);
            Optional.ofNullable(discordTokenMapper.selectByDiscord(0L)).ifPresentOrElse(
                    token -> discordTokenMapper.updateByToken(tokenPO),
                    () -> discordTokenMapper.insert(tokenPO)
            );
            AccessTokenPO accessTokenPO = new AccessTokenPO(tokenDTO.getRefresh_token(), tokenDTO.getAccess_token());
            accessTokenPO.setPlayer_name("CLIENT");
            accessTokenPO.setPlayer_id(0);
            accessTokenPO.setQq_code(0L);
            Optional.ofNullable(tokenMapper.selectByQq_code(0L)).ifPresentOrElse(
                    token -> tokenMapper.updateByToken(accessTokenPO),
                    () -> tokenMapper.insert(accessTokenPO)
            );
            logger.info("successfully created client token: {}", tokenPO.getAccess_token());
        }
        catch (Exception e) {
            logger.error("{} : {}", e.getClass(), e.getMessage());
            throw new LazybotRuntimeException("数据库操作失败，请检查数据库服务是否正常");
        }
    }
    @Scheduled(cron = "0 0 0/12 * * ? ")
    public void refreshPPPlusClientToken()
    {
        String url =  URLBuildUtil.buildURLOfLazybotToken(lazybotClientId,lazybotClientSecret);
        try {
            LazybotWebResult<String> lazybotTokenJSON = JSON.parseObject(
                    HttpUtil.createPost(url).execute().body(),
                    new TypeReference<LazybotWebResult<String>>() {}
            );
            lazybotToken= lazybotTokenJSON.getData();
            logger.info("Lazybot token created: {}",lazybotTokenJSON.getData());
        }
        catch (Exception e) {
            logger.error("{} : {}", e.getClass(), e.getMessage());
            throw new LazybotRuntimeException("更新PP+验证失败，请检查服务器");
        }
    }
    public static String getLazybotToken() {
        if (lazybotToken == null) {
            throw new IllegalStateException("PP+获取Token未初始化！");
        }
        return lazybotToken;
    }


}
