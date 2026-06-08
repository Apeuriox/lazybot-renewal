package me.aloic.lazybot.service.Impl;

import jakarta.annotation.Resource;
import me.aloic.lazybot.entity.dto.geometry.*;
import me.aloic.lazybot.service.GeometryDashService;
import me.aloic.lazybot.parameter.GdSearchParameter;
import me.aloic.lazybot.util.DataExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Geometry Dash 关卡搜索服务实现
 * 核心数据解析逻辑移植自 gdsearch/src/index.ts 的 get_gdinfo() 及相关函数
 * HTTP调用委托给 DataExtractor
 */
@Service
public class GeometryDashServiceImpl implements GeometryDashService
{
    private static final Logger logger = LoggerFactory.getLogger(GeometryDashServiceImpl.class);

    @Resource
    private DataExtractor dataExtractor;

    // ==================== 主搜索方法 ====================

    @Override
    public List<DemonLadderLevelData> searchLevels(GdSearchParameter params) throws Exception
    {
        String postBody = params.buildPostBody();
        logger.info("GD搜索请求: page={}, body={}", params.getPage(), postBody);

        String rawBody = dataExtractor.extractGdSearchLevels(postBody);

        if (rawBody == null || rawBody.isEmpty() || "-1".equals(rawBody.trim())) {
            return List.of(); // 搜索结果为空
        }

        return parseGdResponse(rawBody);
    }


    @Override
    public List<DemonLadderLevelData> parseGdResponse(String rawResponse)
    {
        // rawResponse格式: levelPart#creatorPart#songPart#pageInfoPart
        String[] result = rawResponse.split("#");
        if (result.length < 4) {
            logger.warn("GD返回数据格式异常, 分段数={}", result.length);
            return List.of();
        }

        // --- Part 0: 关卡数据 ---
        // 格式: key:val:key:val|key:val:key:val|... (每54个字段为一组)
        String[] levelParts = result[0].split(":");
        List<String[]> levelStr = new ArrayList<>();
        final int FIELDS_PER_LEVEL = 54;

        for (int i = 0; i < 10; i++) {
            int baseIdx = i * FIELDS_PER_LEVEL;
            if (baseIdx + FIELDS_PER_LEVEL > levelParts.length) break;
            String[] fields = new String[FIELDS_PER_LEVEL];
            for (int j = 0; j < FIELDS_PER_LEVEL && baseIdx + j < levelParts.length; j++) {
                fields[j] = levelParts[baseIdx + j];
            }
            levelStr.add(fields);
        }

        List<LevelRaw> levelRaws = new ArrayList<>();
        for (String[] fields : levelStr) {
            if (fields[1] == null || fields[1].isEmpty()) break; // 没有更多关卡
            LevelRaw obj = new LevelRaw();
            obj.setLevelId(fields[1]);
            obj.setLevelName(fields[3]);
            obj.setGameVersion(fields[5]);
            obj.setPlayerId(fields[7]);
            obj.setDifficultyDenominator(fields[9]);
            obj.setDifficultyNumerator(fields[11]);
            obj.setDownloads(fields[13]);
            obj.setOfficialSong(fields[15]);
            obj.setLevelVersion(fields[17]);
            obj.setLikes(fields[19]);
            obj.setIsDemon(fields[21]);
            obj.setDemonDifficulty(fields[23]);
            obj.setIsAuto(fields[25]);
            obj.setStars(fields[27]);
            obj.setFeatureScore(fields[29]);
            obj.setEpic(fields[31]);
            obj.setObjects(fields[33]);
            obj.setDescription(fields[35]);
            obj.setLength(fields[37]);
            obj.setCopiedId(fields[39]);
            obj.setIsTwoPlayer(fields[41]);
            obj.setCoins(fields[43]);
            obj.setVerifiedCoins(fields[45]);
            obj.setStarsRequested(fields[47]);
            obj.setEditorTime(fields[49]);
            obj.setCopiedEditorTime(fields[51]);
            obj.setCustomSongId(fields[53]);
            levelRaws.add(obj);
        }

        // --- Part 1: 作者数据 ---
        // 格式: key:val:key:val ... 其中val可能包含|分隔 (取后半部分)
        String[] creatorParts = result[1].split(":");
        List<Creator> creatorObjs = new ArrayList<>();
        for (int i = 0; i < creatorParts.length; i++) {
            String part = creatorParts[i];
            if (part.contains("|")) {
                String[] split = part.split("\\|");
                creatorParts[i] = split.length > 1 ? split[1] : part;
            }
        }
        for (int i = 0; i + 1 < creatorParts.length; i += 2) {
            creatorObjs.add(new Creator(creatorParts[i], creatorParts[i + 1]));
        }

        // --- Part 2: 音乐数据 ---
        // 格式: ~|~ 分隔字段, ~:~ 分隔每首音乐
        String[] songEntries = result[2].split("~:~");
        List<LevelSong> levelSongs = new ArrayList<>();
        for (String entry : songEntries) {
            String[] fields = entry.split("~\\|~");
            LevelSong song = new LevelSong();
            if (fields.length > 1) song.setSongId(fields[1]);
            if (fields.length > 3) song.setSongName(fields[3]);
            if (fields.length > 5) song.setArtistId(fields[5]);
            if (fields.length > 7) song.setArtistName(fields[7]);
            if (fields.length > 9) song.setSongSize(fields[9]);
            if (fields.length > 11) song.setVideoId(fields[11]);
            if (fields.length > 13) song.setSongLink(fields[13]);
            if (fields.length > 15) song.setSongYoutubeUrl(fields[15]);
            if (fields.length > 17) song.setIsVerified(fields[17]);
            levelSongs.add(song);
        }

        // --- Part 3: 分页信息 ---
        // 格式: total:offset:amount
        String[] pageParts = result[3].split(":");
        SearchPagination pageInfo = new SearchPagination();
        if (pageParts.length > 0) pageInfo.setTotal(pageParts[0]);
        if (pageParts.length > 1) pageInfo.setOffset(pageParts[1]);
        if (pageParts.length > 2) pageInfo.setAmount(pageParts[2]);

        // --- 组装 DemonLadderLevelData ---
        List<DemonLadderLevelData> levelDataList = new ArrayList<>();
        for (LevelRaw levelRaw : levelRaws) {
            // 匹配作者
            Creator matchedCreator = null;
            for (Creator c : creatorObjs) {
                if (levelRaw.getPlayerId().equals(c.getCreatorId())) {
                    matchedCreator = c;
                    break;
                }
            }
            if (matchedCreator == null) {
                matchedCreator = new Creator(levelRaw.getPlayerId(), "Unknown");
            }

            // 匹配音乐 (通过CustomSongId匹配, 否则尝试OfficialSong)
            LevelSong matchedSong = null;
            for (LevelSong s : levelSongs) {
                if (levelRaw.getCustomSongId().equals(s.getSongId())) {
                    matchedSong = s;
                    break;
                }
            }
            if (matchedSong == null) {
                matchedSong = checkOfficialSong(levelRaw.getOfficialSong());
            }

            levelDataList.add(new DemonLadderLevelData(levelRaw, matchedCreator, matchedSong, pageInfo));
        }

        return levelDataList;
    }

    // ==================== GDDL / Demonlist / Pemonlist (委托给 DataExtractor) ====================

    @Override
    public DemonLadderLevel fetchGddlInfo(String levelId)
    {
        return dataExtractor.extractDemonLadderLevel(levelId);
    }

    @Override
    public String fetchDemonListPlacement(String levelId)
    {
        return dataExtractor.extractDemonListPlacement(levelId);
    }

    @Override
    public String fetchPemonListPlacement(String levelId)
    {
        return dataExtractor.extractPemonListPlacement(levelId);
    }

    // ==================== 工具函数 (移植自TS) ====================

    @Override
    public String checkDifficulty(DemonLadderLevelData levelData)
    {
        LevelRaw level = levelData.getLevel();
        if ("1".equals(level.getIsDemon())) {
            return switch (level.getDemonDifficulty()) {
                case "3" -> "Easy Demon";
                case "4" -> "Medium Demon";
                case "0" -> "Hard Demon";
                case "5" -> "Insane Demon";
                case "6" -> "Extreme Demon";
                default -> "Demon";
            };
        }
        if ("1".equals(level.getIsAuto())) {
            return "Auto";
        }
        if ("0".equals(level.getStars())) {
            return "Unrated";
        }
        return switch (level.getDifficultyNumerator()) {
            case "10" -> "Easy";
            case "20" -> "Normal";
            case "30" -> "Hard";
            case "40" -> "Harder";
            case "50" -> "Insane";
            default -> "Unknown";
        };
    }

    @Override
    public String searchDifficultyDisplay(DemonLadderLevelData levelData)
    {
        LevelRaw level = levelData.getLevel();
        if ("1".equals(level.getIsDemon())) {
            return switch (level.getDemonDifficulty()) {
                case "3" -> "Ezd";
                case "4" -> "Med";
                case "0" -> "Hdd";
                case "5" -> "Insd";
                case "6" -> "Exd";
                default -> "Dem";
            };
        }
        if ("1".equals(level.getIsAuto())) {
            return "Auto";
        }
        if ("0".equals(level.getStars())) {
            return "NA";
        }
        return switch (level.getDifficultyNumerator()) {
            case "10" -> "Easy";
            case "20" -> "Normal";
            case "30" -> "Hard";
            case "40" -> "Harder";
            case "50" -> "Insane";
            default -> "NA";
        };
    }

    @Override
    public String checkFeature(DemonLadderLevelData levelData)
    {
        LevelRaw level = levelData.getLevel();
        if ("0".equals(level.getStars())) {
            return "Unrated";
        }
        if (!"0".equals(level.getEpic())) {
            return switch (level.getEpic()) {
                case "1" -> "Epic";
                case "2" -> "Legendary";
                case "3" -> "Mythic";
                default -> "Featured";
            };
        }
        if (!"0".equals(level.getFeatureScore())) {
            return "Featured";
        }
        return "Rated";
    }

    @Override
    public String checkLength(DemonLadderLevelData levelData)
    {
        return switch (levelData.getLevel().getLength()) {
            case "0" -> "Tiny";
            case "1" -> "Short";
            case "2" -> "Medium";
            case "3" -> "Long";
            case "4" -> "XL";
            case "5" -> "Plat.";
            default -> "Unknown";
        };
    }

    /**
     * 查找官方曲目信息 (移植自TS: CheckSong)
     * @param songNumber GD官方曲目编号 (0-21)
     */
    private LevelSong checkOfficialSong(String songNumber)
    {
        LevelSong song = new LevelSong();
        song.setSongId("Null");
        song.setSongName("Null");
        song.setArtistName("Null");

        switch (songNumber) {
            case "0" -> { song.setSongName("Stereo Madness"); song.setArtistName("ForeverBound"); }
            case "1" -> { song.setSongName("Back On Track"); song.setArtistName("DJVI"); }
            case "2" -> { song.setSongName("Polargeist"); song.setArtistName("Step"); }
            case "3" -> { song.setSongName("Dry Out"); song.setArtistName("DJVI"); }
            case "4" -> { song.setSongName("Base After Base"); song.setArtistName("DJVI"); }
            case "5" -> { song.setSongName("Cant Let Go"); song.setArtistName("DJVI"); }
            case "6" -> { song.setSongName("Jumper"); song.setArtistName("Various Artists"); }
            case "7" -> { song.setSongName("Time Machine"); song.setArtistName("Waterflame"); }
            case "8" -> { song.setSongName("Cycles"); song.setArtistName("Cycles"); }
            case "9" -> { song.setSongName("xStep"); song.setArtistName("DJVI"); }
            case "10" -> { song.setSongName("Clutterfunk"); song.setArtistName("Waterflame"); }
            case "11" -> { song.setSongName("Theory Of Everything"); song.setArtistName("dj-Nate"); }
            case "12" -> { song.setSongName("Electroman Adventures"); song.setArtistName("Waterflame"); }
            case "13" -> { song.setSongName("Club Step"); song.setArtistName("dj-Nate"); }
            case "14" -> { song.setSongName("Electrodynamix"); song.setArtistName("dj-Nate"); }
            case "15" -> { song.setSongName("Hexagon Force"); song.setArtistName("Waterflame"); }
            case "16" -> { song.setSongName("Blast Processing"); song.setArtistName("Waterflame"); }
            case "17" -> { song.setSongName("Theory Of Everything 2"); song.setArtistName("dj-Nate"); }
            case "18" -> { song.setSongName("Geometrical Dominator"); song.setArtistName("Waterflame"); }
            case "19" -> { song.setSongName("Deadlocked"); song.setArtistName("F-777"); }
            case "20" -> { song.setSongName("Fingerdash"); song.setArtistName("MDK"); }
            case "21" -> { song.setSongName("Dash"); song.setArtistName("ForeverBound"); }
        }
        return song;
    }
}
