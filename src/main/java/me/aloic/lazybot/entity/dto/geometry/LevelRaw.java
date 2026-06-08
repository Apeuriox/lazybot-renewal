package me.aloic.lazybot.entity.dto.geometry;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * GD关卡原始属性 (从GD服务器返回的27个字段)
 * 对应 Geometry Dash getGJLevels21.php 的返回数据结构
 */
@Data
@NoArgsConstructor
public class LevelRaw implements Serializable
{
    private String levelId;
    private String levelName;
    private String gameVersion;
    private String playerId;
    private String difficultyDenominator;
    private String difficultyNumerator;
    private String downloads;
    private String officialSong;
    private String levelVersion;
    private String likes;
    private String isDemon;
    private String demonDifficulty;
    private String isAuto;
    private String stars;
    private String featureScore;
    private String epic;
    private String objects;
    private String description;
    private String length;
    private String copiedId;
    private String isTwoPlayer;
    private String coins;
    private String verifiedCoins;
    private String starsRequested;
    private String editorTime;
    private String copiedEditorTime;
    private String customSongId;
}
