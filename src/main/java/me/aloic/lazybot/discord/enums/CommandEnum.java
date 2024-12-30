package me.aloic.lazybot.discord.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import me.aloic.lazybot.discord.entity.CommandOption;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.List;


@Getter
@AllArgsConstructor
public enum CommandEnum {
    HELP(1, "help", "帮助面板指令",
            true, null),

    LINK(2, "link", "osu授权认证指令",
            true, List.of(new CommandOption(OptionType.STRING, "username", "绑定指定用户名", true, false))),

    UNLINK(3, "unlink", "osu撤回授权",
            true, null),

    SCORE(4, "score", "根据指定的Bid，Mod等参数查询用户成绩", true,
            List.of( new CommandOption(OptionType.INTEGER, "bid", "指定查询的Bid", true, false),
                    new CommandOption(OptionType.STRING, "user", "指定查询的用户", false, false),
                    new CommandOption(OptionType.STRING, "mod", "指定查询的Mod", false, false),
                    new CommandOption(OptionType.STRING, "mode", "指定查询的模式", false, false),
                    new CommandOption(OptionType.INTEGER, "version", "指定生成图像的风格(输入0为老版设计，其他任何值为新版)", false, false))),

    PLAY_RECENT(5, "pr", "查询用户最近pass的成绩", true,
            List.of( new CommandOption(OptionType.STRING, "user", "指定查询的用户", false, false),
                    new CommandOption(OptionType.INTEGER, "index", "指定查询的位置", false, false),
                    new CommandOption(OptionType.STRING, "mode", "指定查询的模式", false, false),
                    new CommandOption(OptionType.INTEGER, "version", "指定生成图像的风格(输入0为老版设计，其他任何值为新版)", false, false))),

    RECENT(6, "re", "查询用户最近的成绩", true,
            List.of( new CommandOption(OptionType.STRING, "user", "指定查询的用户", false, false),
                    new CommandOption(OptionType.INTEGER, "index", "指定查询的位置", false, false),
                    new CommandOption(OptionType.STRING, "mode", "指定查询的模式", false, false),
                    new CommandOption(OptionType.INTEGER, "version", "指定生成图像的风格(输入0为老版设计，其他任何值为新版)", false, false))),

    BP_LIST(7, "bplist", "查询指定用户的指定from到to的bp", true,
            List.of(new CommandOption(OptionType.INTEGER, "from", "指定开始的位置", true, false),
            new CommandOption(OptionType.INTEGER, "to", "指定结束的位置", true, false),
                    new CommandOption(OptionType.STRING, "user", "指定查询的用户", false, false),
            new CommandOption(OptionType.STRING, "mode", "指定查询的模式", false, false))),

    BP(8, "bp", "查询指定用户的指定bp", true,
            List.of( new CommandOption(OptionType.STRING, "user", "指定查询的用户", false, false),
                    new CommandOption(OptionType.INTEGER, "index", "指定开始的位置", false, false),
                    new CommandOption(OptionType.STRING, "mode", "指定查询的模式", false, false),
                    new CommandOption(OptionType.INTEGER, "version", "指定生成图像", false, false))),
    BPVS(9, "bpvs", "与指定玩家比对Bp", true,
            List.of( new CommandOption(OptionType.STRING, "target", "指定想要比较的用户", false, false),
                    new CommandOption(OptionType.STRING, "mode", "指定查询的模式", false, false))),
    NO_CHOKE(10, "nochoke", "将全部bp以FC重算", true,
            List.of( new CommandOption(OptionType.STRING, "user", "指定用户", false, false),
                    new CommandOption(OptionType.STRING, "mode", "指定查询的模式", false, false))),
    NO_1_MISS(11, "no1miss", "以FC重算重算<=1miss的bp", true,
            List.of( new CommandOption(OptionType.STRING, "user", "指定用户", false, false),
                    new CommandOption(OptionType.STRING, "mode", "指定查询的模式", false, false))),
    TODAY_BP(12, "todaybp", "查询在指定范围内新增的bp", true,
            List.of( new CommandOption(OptionType.STRING, "user", "指定用户", false, false),
                    new CommandOption(OptionType.STRING, "mode", "指定查询的模式", false, false),
                    new CommandOption(OptionType.STRING, "days", "限定查询天数", false, false))),

    ;
    private final Integer id;

    private final String commandName;

    private final String description;

    private final Boolean valid;

    private final List<CommandOption> options;

}
