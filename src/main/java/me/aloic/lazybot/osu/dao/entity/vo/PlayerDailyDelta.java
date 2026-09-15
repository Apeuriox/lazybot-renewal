package me.aloic.lazybot.osu.dao.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.osu.dao.entity.po.PlayerStatisticsPO;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerDailyDelta
{
    private Double pp;
    private Integer playCount;
    private Long totalHitCount;
    private Double accuracy;
    private Integer globalRank;
    private Integer countryRank;
    private Integer dateDifference;

    public static PlayerDailyDelta empty()
    {
        return new PlayerDailyDelta();
    }

    public static PlayerDailyDelta from(PlayerInfoVO current, PlayerStatisticsPO snapshot)
    {
        if (current == null || snapshot == null) {
            return empty();
        }
        PlayerDailyDelta delta = new PlayerDailyDelta();
        if (current.getPerformancePoint() != null && snapshot.getPerformancePoint() != null) {
            delta.setPp(current.getPerformancePoint() - snapshot.getPerformancePoint());
        }
        if (current.getPlayCount() != null && snapshot.getPlayCount() != null) {
            delta.setPlayCount(current.getPlayCount() - snapshot.getPlayCount());
        }
        if (current.getTotalHitCount() != null && snapshot.getTotalHitCount() != null) {
            delta.setTotalHitCount(current.getTotalHitCount() - snapshot.getTotalHitCount());
        }
        if (current.getCountryRank() != null && snapshot.getCountryRank() != null) {
            delta.setCountryRank(current.getCountryRank() - snapshot.getCountryRank());
        }
        if (current.getGlobalRank() != null && snapshot.getGlobalRank() != null) {
            delta.setGlobalRank(current.getGlobalRank() - snapshot.getGlobalRank());
        }
        if (current.getAccuracy() != null && snapshot.getAccuracy() != null) {
            delta.setAccuracy(current.getAccuracy() - snapshot.getAccuracy());
        }
        if (snapshot.getRecordDateTime() != null) {
            delta.setDateDifference((int) ChronoUnit.DAYS.between(snapshot.getRecordDateTime(), LocalDateTime.now()));
        }
        return delta;
    }
}
