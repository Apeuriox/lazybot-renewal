package me.aloic.lazybot.osu.utils;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.player.Statistics;
import me.aloic.lazybot.osu.dao.entity.po.PlayerStatisticsPO;
import me.aloic.lazybot.osu.dao.entity.po.PlayerStatsWatchPO;
import me.aloic.lazybot.osu.dao.mapper.PlayerStatsWatchMapper;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.enums.SupportedSubServer;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class PlayerStatsWatchManager
{
    @Resource
    private PlayerStatsWatchMapper watchMapper;

    public static boolean isActiveUser(Statistics stats) {
        return stats != null && stats.getGlobal_rank() != null;
    }

    public static boolean isActiveUser(PlayerStatisticsPO row) {
        return row != null && row.getGlobalRank() != null;
    }

    public void recordPlayerActivity(int osuUserId, int mode, int subserver, boolean active)
    {
        watchMapper.upsert(buildRecord(osuUserId, mode, subserver, active));
    }

    public void recordPlayerActivityBatch(List<PlayerStatsWatchPO> watches)
    {
        if (watches == null || watches.isEmpty()) {
            return;
        }
        watchMapper.upsertBatch(watches);
    }

    public static PlayerStatsWatchPO buildRecord(int osuUserId, int mode, int subserver, boolean active)
    {
        PlayerStatsWatchPO watch = new PlayerStatsWatchPO();
        watch.setId(osuUserId);
        watch.setMode(mode);
        watch.setSubserver(subserver);
        watch.setActive(active);
        return watch;
    }

    public void activateIfPlayerIsActive(PlayerInfoDTO player, String requestedMode)
    {
        if (player == null || player.getId() == null) {
            return;
        }
        Integer mode = resolveModeToValue(requestedMode, player);
        if (mode == null || !isActiveUser(player.getStatistics())) {
            return;
        }
        recordPlayerActivity(player.getId(), mode, SupportedSubServer.STABLE.getValue(), true);
    }

    private Integer resolveModeToValue(String requestedMode, PlayerInfoDTO player)
    {
        String raw = (requestedMode == null || requestedMode.isBlank())
                ? player.getPlaymode()
                : requestedMode;
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            OsuMode mode = OsuMode.getMode(raw);
            if (mode == OsuMode.Default) {
                return null;
            }
            return mode.getValue();
        }
        catch (Exception e) {
            log.warn("Cannot resolve snapshot mode from '{}'", raw);
            return null;
        }
    }
}
