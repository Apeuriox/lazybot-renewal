package me.aloic.lazybot.osu.service;

import lombok.extern.slf4j.Slf4j;
import me.aloic.lazybot.monitor.ResourceMonitor;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.osu.dao.mapper.OsuAccountMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * osu! 头像的本地缓存。
 *
 * <p>osu! 的头像 URL 通常保持不变，所以不能再通过比较 URL 判断头像是否更新。
 * 本服务按 TTL 发起带 If-None-Match 的条件请求；TTL 内完全不访问头像服务器，
 * 304 时只刷新检查时间，200 时原子替换本地文件。</p>
 */
@Slf4j
@Service
public class AvatarCacheService
{
    private final OsuAccountMapper osuAccountMapper;
    private final HttpClient httpClient;
    private final Duration revalidateInterval;

    public AvatarCacheService(
            OsuAccountMapper osuAccountMapper,
            @Value("${lazybot.avatar-cache.revalidate-hours:24}") long revalidateHours)
    {
        this.osuAccountMapper = osuAccountMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        this.revalidateInterval = Duration.ofHours(Math.max(1, revalidateHours));
    }

    public String ensureAvatar(PlayerInfoDTO player, UserBindingPO binding)
    {
        Path avatarPath = ResourceMonitor.getResourcePath()
                .resolve("osuFiles/playerAvatar/" + player.getId() + ".jpg")
                .toAbsolutePath();

        if (binding == null || binding.getOsu_account_id() == null)
            return avatarPath.toString();

        LocalDateTime now = LocalDateTime.now();
        if (!Objects.equals(player.getUsername(), binding.getPlayer_name())) {
            osuAccountMapper.updateUsernameCache(
                    binding.getOsu_account_id(), player.getUsername(), now);
        }
        if (Files.exists(avatarPath)
                && binding.getAvatar_next_check_at() != null
                && binding.getAvatar_next_check_at().isAfter(now))
            return avatarPath.toString();

        try
        {
            revalidate(player.getAvatar_url(), avatarPath, binding, now);
        }
        catch (Exception e)
        {
            if (!Files.exists(avatarPath))
                throw new IllegalStateException("下载 osu! 头像失败: " + player.getId(), e);

            // 远端短暂不可用时继续使用陈旧缓存，避免图片生成整体失败。
            log.warn("重新验证 osu! 头像失败，继续使用本地缓存: playerId={}", player.getId(), e);
        }

        return avatarPath.toString();
    }

    private void revalidate(
            String avatarUrl,
            Path avatarPath,
            AccessTokenPO binding,
            LocalDateTime checkedAt) throws IOException, InterruptedException
    {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(URI.create(avatarUrl))
                .timeout(Duration.ofSeconds(20))
                .GET();
        if (Files.exists(avatarPath)
                && binding.getAvatar_etag() != null
                && !binding.getAvatar_etag().isBlank())
            requestBuilder.header("If-None-Match", binding.getAvatar_etag());

        HttpResponse<byte[]> response = httpClient.send(
                requestBuilder.build(),
                HttpResponse.BodyHandlers.ofByteArray());
        LocalDateTime nextCheckAt = checkedAt.plus(revalidateInterval);

        if (response.statusCode() == 304)
        {
            osuAccountMapper.updateAvatarCacheMetadata(
                    binding.getOsu_account_id(),
                    binding.getAvatar_etag(),
                    checkedAt,
                    nextCheckAt);
            return;
        }

        if (response.statusCode() < 200 || response.statusCode() >= 300)
            throw new IOException("头像服务器返回 HTTP " + response.statusCode());

        Files.createDirectories(avatarPath.getParent());
        Path temporary = Files.createTempFile(avatarPath.getParent(), "avatar-", ".tmp");
        try
        {
            Files.write(temporary, response.body());
            try
            {
                Files.move(
                        temporary,
                        avatarPath,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            }
            catch (IOException ignored)
            {
                Files.move(temporary, avatarPath, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        finally
        {
            Files.deleteIfExists(temporary);
        }

        String etag = response.headers().firstValue("ETag").orElse(null);
        osuAccountMapper.updateAvatarCacheMetadata(
                binding.getOsu_account_id(),
                etag,
                checkedAt,
                nextCheckAt);
    }
}
