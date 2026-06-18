//package me.aloic.lazybot.service;
//
//import me.aloic.lazybot.entity.dto.geometry.DemonLadderLevelData;
//import me.aloic.lazybot.entity.dto.geometry.DemonLadderLevel;
//import me.aloic.lazybot.parameter.GdSearchParameter;
//
//import java.util.List;
//
///**
// * Geometry Dash 关卡搜索服务接口
// */
//public interface GeometryDashService
//{
//    /**
//     * 在GD服务器上搜索关卡
//     * @param params 搜索参数
//     * @return 关卡数据列表
//     */
//    List<DemonLadderLevelData> searchLevels(GdSearchParameter params) throws Exception;
//
//    /**
//     * 解析GD服务器原始返回字符串为关卡数据列表
//     * @param rawResponse GD API原始返回
//     * @return 解析后的关卡数据列表
//     */
//    List<DemonLadderLevelData> parseGdResponse(String rawResponse);
//
//    /**
//     * 从GDDL获取关卡的Rating和Enjoyment数据
//     * @param levelId GD关卡ID
//     * @return GDDL数据，失败返回null
//     */
//    DemonLadderLevel fetchGddlInfo(String levelId);
//
//    /**
//     * 从Pointercrate Demonlist获取关卡排名
//     * @param levelId GD关卡ID
//     * @return 排名(如 "#15")，未上榜返回null
//     */
//    String fetchDemonListPlacement(String levelId);
//
//    /**
//     * 从Pemonlist获取Platformer关卡排名
//     * @param levelId GD关卡ID
//     * @return 排名(如 "(#5)")，未上榜返回null
//     */
//    String fetchPemonListPlacement(String levelId);
//
//    /** 判断关卡难度全名 (e.g. "Extreme Demon", "Hard", "Auto") */
//    String checkDifficulty(DemonLadderLevelData levelData);
//
//    /** 判断关卡难度缩写 (e.g. "Exd", "Hdd", "Easy")，用于搜索列表显示 */
//    String searchDifficultyDisplay(DemonLadderLevelData levelData);
//
//    /** 判断关卡质量评级 (Rated/Featured/Epic/Legendary/Mythic) */
//    String checkFeature(DemonLadderLevelData levelData);
//
//    /** 判断关卡长度 (Tiny/Short/Medium/Long/XL/Plat.) */
//    String checkLength(DemonLadderLevelData levelData);
//}
