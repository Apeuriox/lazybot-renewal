package me.aloic.lazybot.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import jakarta.annotation.Resource;
import me.aloic.lazybot.enums.HTTPTypeEnum;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.monitor.TokenMonitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class ApiRequestExecutor
{
    @Resource
    private TokenMonitor tokenMonitor;

    private static final Logger logger = LoggerFactory.getLogger(ApiRequestExecutor.class);

    private final int MAX_RETRIES = 3;

    public <T> T execute(String url,
                         HTTPTypeEnum type,
                         String token,
                         Object body,
                         Class<T> clazz) {
        return doExecute(url, type, token, body, clazz, null);
    }
    public String execute(String url,
                         HTTPTypeEnum type,
                         String token,
                         Object body) {
        return doExecute(url, type, token, body, null, null);
    }


    public <T> T execute(String url,
                         HTTPTypeEnum type,
                         String token,
                         Object body,
                         TypeReference<T> typeRef) {
        return doExecute(url, type, token, body, null, typeRef);
    }

    private <T> T doExecute(String url,
                         HTTPTypeEnum type,
                         String token,
                         Object body,
                         Class<T> clazz,
                         TypeReference<T> typeRef)  {

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                HttpRequest request = createRequest(type, url, token, body);
                HttpResponse response = request.executeAsync();
                int status = response.getStatus();
                String respBody = response.body();
                if (status == 401) {
                    logger.warn("ppy令牌过期. 正在更新...");
                    tokenMonitor.refreshClientToken();
                    tokenMonitor.refreshPPPlusClientToken();
                    TimeUnit.SECONDS.sleep(10);
                    continue;
                }
                if (status >= 200 && status < 300) {
                    response.close();
                    logger.info("HTTP request successful: {}", url);
                    if (clazz != null) return JSON.parseObject(respBody, clazz);
                    else if (typeRef != null) return JSON.parseObject(respBody, typeRef.getType());
                    else return (T) respBody;

                } else {
                    logger.warn("HTTP 请求失败: {}, 状态码: {}, 内容: {}", url, status, respBody);
                    throw new LazybotRuntimeException("HTTP 请求失败, 状态码: " + status);
                }
            } catch (Exception e) {
                logger.error("请求在尝试 {} 次后失败: {}", attempt, e.getMessage());
                if (attempt == MAX_RETRIES) {
                    throw new LazybotRuntimeException("请求在尝试 " + MAX_RETRIES + " 次后失败: " + e.getMessage(), e);
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    throw new LazybotRuntimeException("请求线程中断", interrupted);
                }
            }
        }
        return null;
    }

    private HttpRequest createRequest(HTTPTypeEnum type,
                                      String url,
                                      String token,
                                      Object bodies) {

        HttpRequest request = switch (type) {
            case GET -> HttpUtil.createGet(url);
            case POST -> HttpUtil.createPost(url).body(JSON.toJSONString(bodies));
            case DELETE -> HttpUtil.createRequest(Method.DELETE, url);
        };

        request.addHeaders(setDefaultHeaders(token));
        return request;
    }
    public Map<String, String> setDefaultHeaders(String token){
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("x-api-version","20220705");
        if (token !=null) headers.put("Authorization", String.format("Bearer %s", token));
        return headers;
    }
}
