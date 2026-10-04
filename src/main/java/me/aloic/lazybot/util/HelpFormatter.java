package me.aloic.lazybot.util;

import me.aloic.lazybot.entity.CommandHelp;

public class HelpFormatter {

    public static String format(CommandHelp help) {
        StringBuilder sb = new StringBuilder();

        sb.append("[Lazybot] 命令: ").append(help.getCommand()).append("\n");
        if (help.getCode() != null && !help.getCode().isBlank()) {
            sb.append("编号: ").append(help.getCode()).append("\n");
        }
        if (help.getHeadline() != null && !help.getHeadline().isBlank()) {
            sb.append(help.getHeadline()).append("\n");
        }
        sb.append("调用名: ").append(help.getAlias()).append("\n");
        sb.append("描述: ").append(help.getDescription()).append("\n\n");

        if (!help.getOptions().isEmpty()) {
            sb.append("参数:\n");
            help.getOptions().forEach((params) -> {
                sb.append("  ").append(params.toString()).append("\n");
            });
            sb.append("==========\n");
        }

        if (!help.getUsageExamples().isEmpty()) {
            sb.append("示例:\n");
            help.getUsageExamples().forEach(example -> {
                sb.append("  ").append(example).append("\n");
            });
        }
        sb.append("\n");
        sb.append("作者: ").append(help.getCreator()).append("\n");
        if (help.getDesigner()!=null) sb.append("图形设计: ").append(help.getDesigner()).append("\n");
        sb.append("完成时间: ").append(help.getInitialReleaseDate());
        if (help.getAvailability() != null && !help.getAvailability().isBlank()) {
            sb.append("\n状态: ").append(help.getAvailability());
        }
        return sb.toString().trim();
    }
}
