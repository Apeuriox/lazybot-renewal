//package me.aloic.lazybot.command.geometry;
//
//import com.mikuac.shiro.common.utils.MsgUtils;
//import com.mikuac.shiro.core.Bot;
//import jakarta.annotation.Resource;
//import me.aloic.lazybot.annotation.LazybotCommandMapping;
//import me.aloic.lazybot.command.LazybotSlashCommand;
//import me.aloic.lazybot.component.TestOutputTool;
//import me.aloic.lazybot.entity.CommandHelp;
//import me.aloic.lazybot.entity.CommandParameter;
//import me.aloic.lazybot.entity.dto.geometry.DemonLadderLevelData;
//import me.aloic.lazybot.entity.dto.geometry.SearchPagination;
//import me.aloic.lazybot.service.GeometryDashService;
//import me.aloic.lazybot.parameter.GdSearchParameter;
//import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
//import me.aloic.lazybot.util.HelpFormatter;
//import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
///**
// * GD关卡搜索指令
// * 移植自 gdsearch/src/index.ts 的 gd查询 命令
// *
// * 功能: 在Geometry Dash服务器上搜索关卡，返回关卡列表
// * 图形化渲染部分留空，由后续补充
// */
//@LazybotCommandMapping({"gdsearch", "gd查询", "gdquery"})
//@Component
//public class SearchCommand implements LazybotSlashCommand
//{
//    private static final Logger logger = LoggerFactory.getLogger(SearchCommand.class);
//
//    @Resource
//    private GeometryDashService geometryDashService;
//
//    @Resource
//    private TestOutputTool testOutputTool;
//
//    @Override
//    public void execute(SlashCommandInteractionEvent event) throws Exception
//    {
//        //not impl
//    }
//
//    @Override
//    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
//    {
//        GdSearchParameter params = GdSearchParameter.analyzeParameter(event.getCommandParameters());
//        params.validateParams();
//
//        List<DemonLadderLevelData> results = geometryDashService.searchLevels(params);
//
//        if (results.isEmpty()) {
//            bot.sendGroupMsg(event.getMessageEvent().getGroupId(),
//                    MsgUtils.builder().text("搜索结果为空，请检查关卡名是否输入正确？").build(), false);
//            return;
//        }
//
//        String resultText = buildSearchResultText(results, params);
//        bot.sendGroupMsg(event.getMessageEvent().getGroupId(),
//                MsgUtils.builder().text(resultText).build(), false);
//    }
//
//    // ==================== 本地测试执行 ====================
//
//    @Override
//    public void execute(LazybotSlashCommandEvent event) throws Exception
//    {
//        GdSearchParameter params = GdSearchParameter.analyzeParameter(event.getCommandParameters());
//        params.validateParams();
//
//        List<DemonLadderLevelData> results = geometryDashService.searchLevels(params);
//
//        StringBuilder output = new StringBuilder();
//        if (results.isEmpty()) {
//            output.append("搜索结果为空");
//        } else {
//            output.append(buildSearchResultText(results, params));
//            output.append("\n\n");
//
//            // 打印第一个关卡的详细信息 (用于测试)
//            DemonLadderLevelData first = results.get(0);
//            output.append("=== 首个关卡详细信息 ===\n");
//            output.append("LevelName: ").append(first.getLevel().getLevelName()).append("\n");
//            output.append("LevelID: ").append(first.getLevel().getLevelId()).append("\n");
//            output.append("Creator: ").append(first.getCreator().getCreatorName()).append("\n");
//            output.append("Difficulty: ").append(geometryDashService.checkDifficulty(first)).append("\n");
//            output.append("Stars: ").append(first.getLevel().getStars()).append("\n");
//            output.append("Feature: ").append(geometryDashService.checkFeature(first)).append("\n");
//            output.append("Length: ").append(geometryDashService.checkLength(first)).append("\n");
//            output.append("Downloads: ").append(formatNumber(first.getLevel().getDownloads())).append("\n");
//            output.append("Likes: ").append(formatNumber(first.getLevel().getLikes())).append("\n");
//            output.append("Song: ").append(first.getSong().getSongName())
//                    .append(" - ").append(first.getSong().getArtistName()).append("\n");
//            output.append("SongID: ").append(first.getSong().getSongId()).append("\n");
//            output.append("Total: ").append(first.getPageInfo().getTotal()).append("\n");
//
//            // 查询GDDL信息
//            output.append("\n=== GDDL信息 ===\n");
//            var gddlInfo = geometryDashService.fetchGddlInfo(first.getLevel().getLevelId());
//            if (gddlInfo != null) {
//                output.append("Rating: ").append(String.format("%.2f", gddlInfo.getRating())).append("\n");
//                output.append("Enjoyment: ").append(String.format("%.2f", gddlInfo.getEnjoyment())).append("\n");
//            } else {
//                output.append("无GDDL数据\n");
//            }
//
//            // 查询排名
//            output.append("\n=== 排名信息 ===\n");
//            if ("5".equals(first.getLevel().getLength())) {
//                String pemonPlacement = geometryDashService.fetchPemonListPlacement(first.getLevel().getLevelId());
//                output.append("Pemonlist: ").append(pemonPlacement != null ? pemonPlacement : "未上榜").append("\n");
//            } else {
//                String demonPlacement = geometryDashService.fetchDemonListPlacement(first.getLevel().getLevelId());
//                output.append("Demonlist: ").append(demonPlacement != null ? demonPlacement : "未上榜").append("\n");
//            }
//
//            // TODO: 关卡卡片渲染 (图形化部分留空)
//            output.append("\n[关卡卡片渲染功能待实现]");
//        }
//
//        testOutputTool.writeStringToFile(output.toString());
//    }
//
//    // ==================== 帮助文档 ====================
//
//    @Override
//    public String getHelp()
//    {
//        return HelpFormatter.format(
//                new CommandHelp("GD Search", "gdsearch, gd查询, gdquery",
//                        "在Geometry Dash服务器上搜索关卡，返回关卡列表。图形化卡片功能开发中。",
//                        "Aloic", null, "2026-06-08")
//                        .addExample("/gdsearch bloodbath")
//                        .addExample("/gdsearch --all -d 5 bloodbath  (搜索所有Extreme Demon)")
//                        .addExample("/gdsearch -u 5 -l 3 cataclysm  (搜索Insane难度Long长度)")
//                        .addOption(new CommandParameter("SearchString", "搜索的关卡名称 (仅英文/数字)", CommandParameter.ParameterType.MUST))
//                        .addOption(new CommandParameter("--all, -a", "关闭rated only过滤器，搜索所有关卡", CommandParameter.ParameterType.OPTIONAL))
//                        .addOption(new CommandParameter("--demon, -d <N>", "Demon难度过滤: 1=EzD, 2=Med, 3=Hdd, 4=Insd, 5=Exd", CommandParameter.ParameterType.OPTIONAL))
//                        .addOption(new CommandParameter("--diff, -u <N>", "非Demon难度过滤: 1=Easy ~ 5=Insane", CommandParameter.ParameterType.OPTIONAL))
//                        .addOption(new CommandParameter("--length, -l <N>", "长度过滤: 0=Tiny ~ 4=XL, 5=Plat", CommandParameter.ParameterType.OPTIONAL))
//        );
//    }
//
//    // ==================== 私有工具方法 ====================
//
//    /**
//     * 构建搜索结果文本列表 (移植自TS的关卡列表展示逻辑)
//     */
//    private String buildSearchResultText(List<DemonLadderLevelData> results, GdSearchParameter params)
//    {
//        StringBuilder sb = new StringBuilder();
//        int totalResults = results.size();
//        // 最多显示10个结果
//        int displayCount = Math.min(totalResults, 10);
//
//        for (int i = 0; i < displayCount; i++) {
//            DemonLadderLevelData level = results.get(i);
//            String difficulty = geometryDashService.searchDifficultyDisplay(level);
//            String creator = level.getCreator().getCreatorName();
//            String isPlat = "5".equals(level.getLevel().getLength()) ? "    ☾" : "";
//
//            sb.append(i + 1).append(". ")
//                    .append(level.getLevel().getLevelName())
//                    .append("(").append(difficulty).append(")")
//                    .append(" by ").append(creator)
//                    .append(isPlat).append("\n");
//        }
//
//        // 分页和统计信息
//        SearchPagination pageInfo = results.getFirst().getPageInfo();
//        sb.append("\n(").append(displayCount)
//                .append(" of ").append(pageInfo.getTotal() != null ? pageInfo.getTotal() : "?")
//                .append(")");
//
//        if (params.getAll() == null || !params.getAll()) {
//            sb.append("\n当前搜索为仅限rated关卡，相关参数请输入 gdsearch -h");
//        }
//        sb.append("\n图形化卡片功能开发中，敬请期待~");
//
//        return sb.toString();
//    }
//
//    /**
//     * 格式化数字：添加千位分隔符 (移植自TS: replace(/\B(?=(\d{3})+(?!\d))/g, ','))
//     */
//    private String formatNumber(String numberStr)
//    {
//        if (numberStr == null || numberStr.isEmpty()) return "0";
//        try {
//            long num = Long.parseLong(numberStr);
//            return String.format("%,d", num);
//        } catch (NumberFormatException e) {
//            return numberStr;
//        }
//    }
//}
