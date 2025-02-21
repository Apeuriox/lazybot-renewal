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
                    new CommandOption(OptionType.INTEGER, "version", "指定生成图像的风格", false, false))),

    PLAY_RECENT(5, "rp", "查询用户最近pass的成绩", true,
            List.of( new CommandOption(OptionType.STRING, "user", "指定查询的用户", false, false),
                    new CommandOption(OptionType.INTEGER, "index", "指定查询的位置", false, false),
                    new CommandOption(OptionType.STRING, "mode", "指定查询的模式", false, false),
                    new CommandOption(OptionType.INTEGER, "version", "指定生成图像的风格", false, false))),

    RECENT(6, "rs", "查询用户最近的成绩", true,
            List.of( new CommandOption(OptionType.STRING, "user", "指定查询的用户", false, false),
                    new CommandOption(OptionType.INTEGER, "index", "指定查询的位置", false, false),
                    new CommandOption(OptionType.STRING, "mode", "指定查询的模式", false, false),
                    new CommandOption(OptionType.INTEGER, "version", "指定生成图像的风格", false, false))),

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
    CARD(13, "card", "生成指定玩家的card", true,
            List.of( new CommandOption(OptionType.STRING, "user", "指定用户", false, false),
                    new CommandOption(OptionType.STRING, "mode", "指定查询的模式", false, false))),
    NAME_TO_ID(14, "nametoid", "将指定玩家名序列转化为UID", true,
            List.of( new CommandOption(OptionType.STRING, "list", "指定玩家名列表，分隔符为 , ", true, false))),
    PP_MAP(15, "ppmap", "查询Osu track数据并绘制散点图", true,
            List.of( new CommandOption(OptionType.STRING, "user", "指定用户", false, false),
                    new CommandOption(OptionType.STRING, "mode", "指定查询的模式", false, false))),
    RECOMMEND_DIFFICULTY(16, "rd", "查询指定模式下的推荐星数", true,
            List.of( new CommandOption(OptionType.STRING, "user", "指定用户", false, false),
                    new CommandOption(OptionType.STRING, "mode", "指定查询的模式", false, false))),
    BP_IF(17, "bpif", "按照指定mod重算全部bp", true,
            List.of( new CommandOption(OptionType.STRING, "operator", "指定运算符: +为插入, -为删除, ！为替换", true, false),
                    new CommandOption(OptionType.STRING, "mods", "指定mod组合，使用缩写且不含空格，例: HDHR, 冲突以及重复添加会被忽略", true, false),
                    new CommandOption(OptionType.STRING, "user", "指定用户", false, false),
                    new CommandOption(OptionType.STRING, "mode", "指定查询的模式", false, false),
                    new CommandOption(OptionType.STRING, "rendersize", "指定输出图形最多渲染数量，默认30", false, false))),
    TOP_SCORES(18, "topscores", "查询指定模式下的最高PP成绩", true,
          List.of( new CommandOption(OptionType.STRING, "mode", "指定模式", false, false),
                    new CommandOption(OptionType.STRING, "limit", "指定最大显示数量，默认为10", false, false))),
    BP_CARD(19, "bpcard", "查询指定用户的指定from到to的bp，以卡片列表形式输出", true,
            List.of(new CommandOption(OptionType.INTEGER, "from", "指定开始的位置", true, false),
                    new CommandOption(OptionType.INTEGER, "to", "指定结束的位置", true, false),
                    new CommandOption(OptionType.STRING, "user", "指定查询的用户", false, false),
                    new CommandOption(OptionType.STRING, "mode", "指定查询的模式", false, false))),
    UPDATE(19, "update", "更新指定类型缓存数据", true,
            List.of(new CommandOption(OptionType.INTEGER, "type", "指定类型", true, false),
                    new CommandOption(OptionType.STRING, "user", "指定查询的用户", false, false))),
    TIPS(19, "tips", "获取一个Aloic的小提示", true,
            List.of(new CommandOption(OptionType.INTEGER, "id", "指定id", false, false))),
    SET_MODE(20, "setmode", "更新默认模式", true,
            List.of(new CommandOption(OptionType.STRING, "mode", "指定模式", true, false))),
    VERIFY_MAP(20, "verifymap", "(需要权限) 验证.osu缓存是否过期", true,
            List.of(new CommandOption(OptionType.INTEGER, "bid", "指定bid", true, false))),
    ;
    private final Integer id;

    private final String commandName;

    private final String description;

    private final Boolean valid;

    private final List<CommandOption> options;

}
