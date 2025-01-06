package me.aloic.lazybot.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@Data
@NoArgsConstructor
public class ApiRequestStarter
{
    private Map<String, String> headers;

    private Object body;

    private Map<String, Object> params;

    private Map<String, Object> bodies;

    private StringBuilder url;

    private static final Logger logger = LoggerFactory.getLogger(ApiRequestStarter.class);

    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 10, 30, TimeUnit.SECONDS
            , new SynchronousQueue<Runnable>(true)
            , Executors.defaultThreadFactory());

    public void setUrl(String url) {
        this.url = new StringBuilder(url);
    }

    public void setUrl(StringBuilder sb) {
        this.url = sb;
    }

    public ApiRequestStarter(String url){
        this.url = new StringBuilder(url);
    }
    public ApiRequestStarter(String url, String token){
        this.url=new StringBuilder(url);
        this.setOauth(token);
    }

    public void setDefaultHeaders(){
        //addHeader("Accept", "application/json");
        if(headers == null) {
            headers = new HashMap<>();
        }
        headers.put("Content-Type", "application/json");
        headers.put("x-api-version","20220705");
    }


    public void addHeader(String headerName, String content){
        if(headers == null)
            headers = new HashMap<>();
        setDefaultHeaders();
        headers.put(headerName, content);
    }

    /**
     * 添加http get请求参数
     * @param paramName
     * @param content
     */
    public void addParams(String paramName, Object content){
        if(params == null)
            params = new HashMap<>();
        url.append(String.format("&%s=%s", paramName, content.toString()));
        params.put(paramName, content);


    }

    /**
     * 添加body体（仅post使用）
     * @param bodyName
     * @param obj
     */
    public void addBodies(String bodyName, Object obj){
        if(bodies == null)
            bodies = new HashMap<>();
        bodies.put(bodyName, obj);
    }


    /**
     * osu oauth相关header 添加后可根据token自动查询对应player信息
     * @param token
     */
    public void setOauth(String token){
        addHeader("Authorization", String.format("Bearer %s", token));
    }

    public ApiRequestStarter withOauth(String token) {
        return withHeader("Authorization", "Bearer " + token);
    }
    public ApiRequestStarter withHeader(String name, String value) {
        if(headers == null) {
            headers = new HashMap<>();
        }
        headers.put(name, value);
        return this;
    }


    /**
     * 发送http请求，并将结果包装为resultClass的一个对象。
     * @param type
     * @param resultClass
     * @return
     * @param <T>
     * @throws Exception
     */
    public <T>T executeRequest(String type, Class<T> resultClass){
        if(ContentUtil.HTTP_REQUEST_TYPE_POST.equals(type)){

//            threadPoolExecutor.execute();
            HttpRequest request = HttpUtil.createPost(url.toString());
            HttpResponse response = request.addHeaders(headers).body(JSON.toJSONString(bodies)).executeAsync();
            String resp = response.body();
//            handleHttpCode(response.getStatus());
            T res = JSON.parseObject(resp, resultClass);
            response.close();
            logger.info("POST {} successfully with code of {}", this.getUrl(), response.getStatus());
            return res;

        }
        if(ContentUtil.HTTP_REQUEST_TYPE_GET.equals(type)) {
            int reties=3;
            int currentAttempt = 0;
            while (currentAttempt < reties) {
                try {
                    HttpResponse response = HttpUtil.createGet(url.toString()).addHeaders(headers).executeAsync();
                    String resp = response.body();
//                    handleHttpCode(response.getStatus());
                    T res = JSON.parseObject(resp, resultClass);
                    response.close();
                    logger.info("GET {} successfully with code of {}", this.getUrl(), response.getStatus());
                    return res;
                }
                catch (Exception e) {
                    currentAttempt++;
                    if (currentAttempt < reties) {
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        }
                        catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException("Thread interrupted while retrying", ie);
                        }
                    }
                    else {
                        throw new RuntimeException("Failed to execute HTTP REQUEST GET after " +reties +" retries:" + e.getMessage());
                    }
                }
            }
        }
        if(ContentUtil.HTTP_REQUEST_TYPE_DELETE.equals(type)) {

            HttpUtil.createRequest(Method.DELETE, url.toString()).addHeaders(headers).executeAsync().body();
        }
        return null;
    }

    public <T>T executePost(Object requestParam, Class<T> resultClass) throws Exception{
        String body = JSON.toJSONString(requestParam);
        HttpResponse response = HttpUtil.createPost(url.toString()).addHeaders(headers).body(JSON.toJSONString(requestParam)).executeAsync();
        String resp = response.body();
//        System.out.println("api request resp: " + resp);
        T res = JSON.parseObject(resp, resultClass);
        return res;
    }

    public <T> List<T> executeRequestForList(String type, Class<T> resultClass){
        if(ContentUtil.HTTP_REQUEST_TYPE_POST.equals(type)){
            String resp = HttpUtil.createPost(url.toString()).addHeaders(headers).body(JSON.toJSONString(bodies)).execute().body();
//            System.out.println("api request resp: " + resp);
            return JSON.parseArray(resp, resultClass);

        }
        if(ContentUtil.HTTP_REQUEST_TYPE_GET.equals(type)) {
            int reties=3;
            int currentAttempt = 0;
            while (currentAttempt < reties) {
                try {
                    HttpResponse response = HttpUtil.createGet(url.toString()).addHeaders(headers).execute();
//                    handleHttpCode(response.getStatus());
                    List<T> res = JSON.parseArray(response.body(), resultClass);
                    response.close();
                    logger.info("<list> GET {} successfully with code of {}", this.getUrl(), response.getStatus());
                    return res;
                }
                catch (Exception e) {
                    logger.warn("<list> GET {} failed", this.getUrl());
                    currentAttempt++;
                    if (currentAttempt < reties) {
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        }
                        catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException("Thread interrupted while retrying", ie);
                        }
                    }
                    else {
                        throw new RuntimeException("Failed to execute HTTP REQUEST GET after " +reties +" retries:" + e.getMessage());
                    }
                }
            }
            return null;
        }
        if(ContentUtil.HTTP_REQUEST_TYPE_DELETE.equals(type)) {
            HttpUtil.createRequest(Method.DELETE, url.toString()).addHeaders(headers).execute().body();
        }
        return new ArrayList<>();
    }
//    private void handleHttpCode(int statusCode)
//    {
//        if(statusCode == 401){
//            TokenMonitor.refreshClientToken();
//            logger.info("401 Unauthorized/客户端API Token过期，正在重新获取");
//        }
//    }
}
