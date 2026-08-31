package me.aloic.lazybot.tencent.api;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.monitor.TokenMonitor;
import me.aloic.lazybot.tencent.event.TencentScene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;

@Component
public class TencentOpenApiClient
{
    private static final Logger logger = LoggerFactory.getLogger(TencentOpenApiClient.class);
    private static final int IMAGE_FILE_TYPE = 1;
    private static final int MD5_10M_BYTES = 10_002_432;

    private final TokenMonitor tokenMonitor;
    private final HttpClient httpClient;
    private final String apiBase;

    public TencentOpenApiClient(
            TokenMonitor tokenMonitor,
            @Value("${tencent.bot.sandbox:false}") boolean sandbox,
            @Value("${tencent.bot.api-base:}") String apiBaseOverride)
    {
        this.tokenMonitor = tokenMonitor;
        this.apiBase = resolveApiBase(sandbox, apiBaseOverride);
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        logger.info("Tencent OpenAPI 基址: {}", this.apiBase);
    }

    public String getGatewayUrl()
    {
        JSONObject json = requestJson("GET", "/gateway/bot", null, true);
        logger.info("Tencent Gateway 接入点响应: {}", json);
        String url = json.getString("url");
        if (url == null || url.isBlank()) {
            throw new LazybotRuntimeException("获取 QQ Gateway 地址失败: " + json);
        }
        return url;
    }

    public void sendText(TencentScene scene, String targetOpenid, String messageId, int msgSeq, String content)
    {
        JSONObject body = new JSONObject();
        body.put("msg_type", 0);
        body.put("content", content == null ? "" : content);
        attachReply(body, messageId, msgSeq);
        postMessage(scene, targetOpenid, body);
    }

    public void sendImage(TencentScene scene, String targetOpenid, String messageId, int msgSeq, byte[] imageBytes)
    {
        if (imageBytes == null || imageBytes.length == 0) {
            throw new LazybotRuntimeException("图片内容为空");
        }
        String fileInfo = uploadImage(scene, targetOpenid, imageBytes);
        JSONObject media = new JSONObject();
        media.put("file_info", fileInfo);
        JSONObject body = new JSONObject();
        body.put("msg_type", 7);
        body.put("media", media);
        attachReply(body, messageId, msgSeq);
        try {
            postMessage(scene, targetOpenid, body);
        }
        catch (LazybotRuntimeException first) {
            String message = first.getMessage();
            if (message != null && (message.contains("22006") || message.contains("消息类型"))) {
                body.put("content", " ");
                postMessage(scene, targetOpenid, body);
                return;
            }
            throw first;
        }
    }

    public void sendEventText(TencentScene scene, String targetOpenid, String eventId, String content)
    {
        JSONObject body = new JSONObject();
        body.put("msg_type", 0);
        body.put("content", content);
        body.put("event_id", eventId);
        body.put("msg_seq", 1);
        postMessage(scene, targetOpenid, body);
    }

    private String uploadImage(TencentScene scene, String targetOpenid, byte[] imageBytes)
    {
        String fileName = isJpeg(imageBytes) ? "lazybot.jpg" : "lazybot.png";
        try {
            return uploadFileData(scene, targetOpenid, fileName, imageBytes);
        }
        catch (Exception e) {
            logger.warn("file_data 上传失败，改用分片上传: {}", e.getMessage());
            return uploadChunked(scene, targetOpenid, fileName, imageBytes);
        }
    }

    private String uploadFileData(TencentScene scene, String targetOpenid, String fileName, byte[] imageBytes)
    {
        JSONObject body = new JSONObject();
        body.put("file_type", IMAGE_FILE_TYPE);
        body.put("file_name", fileName);
        body.put("srv_send_msg", false);
        body.put("file_data", Base64.getEncoder().encodeToString(imageBytes));
        JSONObject json = requestJson("POST", filesPath(scene, targetOpenid), body, true);
        return requireFileInfo(json);
    }

    private String uploadChunked(TencentScene scene, String targetOpenid, String fileName, byte[] imageBytes)
    {
        try {
            String md5 = hexDigest("MD5", imageBytes);
            String sha1 = hexDigest("SHA-1", imageBytes);
            int headLen = Math.min(MD5_10M_BYTES, imageBytes.length);
            byte[] head = new byte[headLen];
            System.arraycopy(imageBytes, 0, head, 0, headLen);
            String md5_10m = hexDigest("MD5", head);

            JSONObject prepareBody = new JSONObject();
            prepareBody.put("file_type", IMAGE_FILE_TYPE);
            prepareBody.put("file_size", String.valueOf(imageBytes.length));
            prepareBody.put("file_name", fileName);
            prepareBody.put("md5", md5);
            prepareBody.put("sha1", sha1);
            prepareBody.put("md5_10m", md5_10m);
            JSONObject prepared = requestJson(
                    "POST", uploadPreparePath(scene, targetOpenid), prepareBody, true);
            String uploadId = prepared.getString("upload_id");
            if (uploadId == null || uploadId.isBlank()) {
                throw new LazybotRuntimeException("分片预上传未返回 upload_id");
            }
            int blockSize = parsePositive(prepared.get("block_size"), imageBytes.length);
            JSONArray parts = prepared.getJSONArray("parts");
            if (parts == null || parts.isEmpty()) {
                throw new LazybotRuntimeException("分片预上传未返回 parts");
            }
            for (int i = 0; i < parts.size(); i++) {
                JSONObject part = parts.getJSONObject(i);
                int index = part.getIntValue("index", i);
                int partSize = parsePositive(part.get("block_size"), blockSize);
                int offset = index * blockSize;
                if (offset >= imageBytes.length) {
                    continue;
                }
                int length = Math.min(partSize, imageBytes.length - offset);
                byte[] chunk = new byte[length];
                System.arraycopy(imageBytes, offset, chunk, 0, length);
                putPresigned(part.getString("presigned_url"), chunk);
                JSONObject finish = new JSONObject();
                finish.put("upload_id", uploadId);
                finish.put("part_index", index);
                finish.put("block_size", String.valueOf(length));
                finish.put("md5", hexDigest("MD5", chunk));
                requestJson("POST", uploadPartFinishPath(scene, targetOpenid), finish, true);
            }

            JSONObject merge = new JSONObject();
            merge.put("file_type", IMAGE_FILE_TYPE);
            merge.put("srv_send_msg", false);
            merge.put("file_name", fileName);
            merge.put("upload_id", uploadId);
            JSONObject json = requestJson("POST", filesPath(scene, targetOpenid), merge, true);
            return requireFileInfo(json);
        }
        catch (LazybotRuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new LazybotRuntimeException("分片上传图片失败", e);
        }
    }

    private void putPresigned(String url, byte[] chunk)
    {
        if (url == null || url.isBlank()) {
            throw new LazybotRuntimeException("分片预签名 URL 为空");
        }
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(60))
                .PUT(HttpRequest.BodyPublishers.ofByteArray(chunk))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new LazybotRuntimeException(
                        "分片 PUT 失败，HTTP " + response.statusCode() + ": " + trim(response.body()));
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LazybotRuntimeException("分片 PUT 被中断", e);
        }
        catch (LazybotRuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new LazybotRuntimeException("分片 PUT 失败", e);
        }
    }

    private void postMessage(TencentScene scene, String targetOpenid, JSONObject body)
    {
        requestJson("POST", messagesPath(scene, targetOpenid), body, true);
    }

    private JSONObject requestJson(String method, String path, JSONObject body, boolean retryUnauthorized)
    {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(apiBase + path))
                .timeout(Duration.ofSeconds(60))
                .header("Authorization", "QQBot " + TokenMonitor.getTencentToken())
                .header("X-Union-Appid", tokenMonitor.getTencentAppId());
        if ("GET".equals(method)) {
            builder.GET();
        }
        else {
            builder.header("Content-Type", "application/json; charset=utf-8");
            builder.method(
                    method,
                    HttpRequest.BodyPublishers.ofString(
                            body == null ? "{}" : body.toJSONString(),
                            StandardCharsets.UTF_8));
        }
        try {
            HttpResponse<String> response = httpClient.send(
                    builder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            int status = response.statusCode();
            if (status == 401 && retryUnauthorized) {
                tokenMonitor.refreshTencentToken();
                return requestJson(method, path, body, false);
            }
            JSONObject json = parseObject(response.body());
            if (status < 200 || status >= 300 || isApiError(json)) {
                throw new LazybotRuntimeException(formatApiError(status, json, response.body()));
            }
            return json == null ? new JSONObject() : json;
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LazybotRuntimeException("调用 QQ OpenAPI 被中断", e);
        }
        catch (LazybotRuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new LazybotRuntimeException("调用 QQ OpenAPI 失败: " + path, e);
        }
    }

    private static void attachReply(JSONObject body, String messageId, int msgSeq)
    {
        if (messageId != null && !messageId.isBlank()) {
            body.put("msg_id", messageId);
        }
        body.put("msg_seq", msgSeq);
    }

    private static String messagesPath(TencentScene scene, String targetOpenid)
    {
        return scene == TencentScene.GROUP
                ? "/v2/groups/" + targetOpenid + "/messages"
                : "/v2/users/" + targetOpenid + "/messages";
    }

    private static String filesPath(TencentScene scene, String targetOpenid)
    {
        return scene == TencentScene.GROUP
                ? "/v2/groups/" + targetOpenid + "/files"
                : "/v2/users/" + targetOpenid + "/files";
    }

    private static String uploadPreparePath(TencentScene scene, String targetOpenid)
    {
        return scene == TencentScene.GROUP
                ? "/v2/groups/" + targetOpenid + "/upload_prepare"
                : "/v2/users/" + targetOpenid + "/upload_prepare";
    }

    private static String uploadPartFinishPath(TencentScene scene, String targetOpenid)
    {
        return scene == TencentScene.GROUP
                ? "/v2/groups/" + targetOpenid + "/upload_part_finish"
                : "/v2/users/" + targetOpenid + "/upload_part_finish";
    }

    private static String requireFileInfo(JSONObject json)
    {
        String fileInfo = json.getString("file_info");
        if (fileInfo == null || fileInfo.isBlank()) {
            throw new LazybotRuntimeException("上传图片未返回 file_info: " + json);
        }
        return fileInfo;
    }

    private static boolean isApiError(JSONObject json)
    {
        if (json == null) {
            return false;
        }
        if (json.containsKey("err_code") && json.getIntValue("err_code") != 0) {
            return true;
        }
        if (json.containsKey("code") && json.getIntValue("code") != 0 && !json.containsKey("file_info") && !json.containsKey("url")) {
            return json.getIntValue("code") >= 100;
        }
        return false;
    }

    private static String formatApiError(int status, JSONObject json, String raw)
    {
        if (json != null) {
            String message = json.getString("message");
            Object err = json.get("err_code") != null ? json.get("err_code") : json.get("code");
            if (message != null) {
                return "QQ OpenAPI 错误 HTTP " + status + " code=" + err + " " + message;
            }
        }
        return "QQ OpenAPI 错误 HTTP " + status + ": " + trim(raw);
    }

    private static JSONObject parseObject(String body)
    {
        if (body == null || body.isBlank()) {
            return new JSONObject();
        }
        try {
            return JSON.parseObject(body);
        }
        catch (Exception e) {
            JSONObject fallback = new JSONObject();
            fallback.put("raw", body);
            return fallback;
        }
    }

    private static boolean isJpeg(byte[] bytes)
    {
        return bytes.length >= 2
                && (bytes[0] & 0xFF) == 0xFF
                && (bytes[1] & 0xFF) == 0xD8;
    }

    private static String hexDigest(String algorithm, byte[] data) throws Exception
    {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        return HexFormat.of().formatHex(digest.digest(data)).toLowerCase(Locale.ROOT);
    }

    private static int parsePositive(Object raw, int fallback)
    {
        if (raw == null) {
            return fallback;
        }
        try {
            int value = Integer.parseInt(String.valueOf(raw));
            return value > 0 ? value : fallback;
        }
        catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static String resolveApiBase(boolean sandbox, String override)
    {
        if (override != null && !override.isBlank()) {
            return trimSlash(override);
        }
        return sandbox
                ? "https://sandbox.api.sgroup.qq.com"
                : "https://api.sgroup.qq.com";
    }

    private static String trimSlash(String value)
    {
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    private static String trim(String body)
    {
        if (body == null) {
            return "";
        }
        return body.length() > 400 ? body.substring(0, 400) + "..." : body;
    }
}
