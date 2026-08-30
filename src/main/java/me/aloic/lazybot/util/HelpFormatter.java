package me.aloic.lazybot.util;

import me.aloic.lazybot.entity.CommandHelp;

public class HelpFormatter {

    public static String format(CommandHelp help) {
        StringBuilder sb = new StringBuilder();

        sb.append("[Lazybot] 命令: ").append(help.getCommand()).append("\n");
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
        return sb.toString().trim();
    }

    public static String panelDescription(String helpText, int maxChars)
    {
        if (helpText == null || helpText.isBlank()) {
            return "";
        }
        String description = null;
        for (String line : helpText.split("\\R")) {
            String trimmed = line.trim();
            if (trimmed.startsWith("描述:")) {
                description = trimmed.substring("描述:".length()).trim();
                break;
            }
        }
        if (description == null || description.isBlank()) {
            description = helpText.replace("[Lazybot]", "").strip();
            int newline = description.indexOf('\n');
            if (newline >= 0) {
                description = description.substring(0, newline).strip();
            }
        }
        if (description.length() <= maxChars) {
            return description;
        }
        return description.substring(0, maxChars);
    }
}
