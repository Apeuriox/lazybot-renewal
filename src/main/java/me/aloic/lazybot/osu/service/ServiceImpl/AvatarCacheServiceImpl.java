package me.aloic.lazybot.osu.service.ServiceImpl;

import lombok.extern.slf4j.Slf4j;
import me.aloic.lazybot.monitor.ResourceMonitor;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.osu.dao.mapper.OsuAccountMapper;
import me.aloic.lazybot.osu.service.AvatarCacheService;
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

@Slf4j
@Service
public class AvatarCacheServiceImpl implements AvatarCacheService
{
    private final OsuAccountMapper osuAccountMapper;
    private final HttpClient httpClient;
    private final Duration revalidateInterval;

    public AvatarCacheServiceImpl(OsuAccountMapper osuAccountMapper,
                                  @Value("${lazybot.avatar-cache.revalidate-hours:120}") long revalidateHours)
    {
        this.osuAccountMapper = osuAccountMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        this.revalidateInterval = Duration.ofHours(Math.max(1, revalidateHours));
    }

    @Override
    public String ensureAvatar(PlayerInfoDTO player, UserBindingPO binding)
    {
        Path avatarPath = ResourceMonitor.getResourcePath()
                .resolve("osuFiles/playerAvatar/" + player.getId() + ".jpg")
                .toAbsolutePath();

        if (binding == null || binding.getOsu_account_id() == null)
            return avatarPath.toString();

        LocalDateTime now = LocalDateTime.now();
        if (!player.getUsername().equalsIgnoreCase(binding.getPlayer_name())) {
            osuAccountMapper.updateUsernameCache(binding.getOsu_account_id(), player.getUsername(), now);
        }
        if (Files.exists(avatarPath)
                && binding.getAvatar_next_check_at() != null
                && binding.getAvatar_next_check_at().isAfter(now))
            return avatarPath.toString();

        try {
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

    private void revalidate(String avatarUrl,
                            Path avatarPath,
                            UserBindingPO binding,
                            LocalDateTime checkedAt) throws IOException, InterruptedException
    {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(URI.create(avatarUrl)).timeout(Duration.ofSeconds(20)).GET();
        if (Files.exists(avatarPath)
                && binding.getAvatar_etag() != null
                && !binding.getAvatar_etag().isBlank())
            requestBuilder.header("If-None-Match", binding.getAvatar_etag());

        HttpResponse<byte[]> response = httpClient.send(
                requestBuilder.build(),
                HttpResponse.BodyHandlers.ofByteArray());
        LocalDateTime nextCheckAt = checkedAt.plus(revalidateInterval);

        if (response.statusCode() == 304) {
            osuAccountMapper.updateAvatarCacheMetadata(binding.getOsu_account_id(), binding.getAvatar_etag(), checkedAt, nextCheckAt);
            return;
        }

        if (response.statusCode() < 200 || response.statusCode() >= 300)
            throw new IOException("头像服务器返回 HTTP " + response.statusCode());

        Files.createDirectories(avatarPath.getParent());
        Path temporary = Files.createTempFile(avatarPath.getParent(), "avatar-", ".tmp");
        try {
            Files.write(temporary, response.body());
            try {
                Files.move(temporary, avatarPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            }
            catch (IOException ignored) {
                Files.move(temporary, avatarPath, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        finally {
            Files.deleteIfExists(temporary);
        }

        String etag = response.headers().firstValue("ETag").orElse(null);
        osuAccountMapper.updateAvatarCacheMetadata(binding.getOsu_account_id(), etag, checkedAt, nextCheckAt);
    }
}
