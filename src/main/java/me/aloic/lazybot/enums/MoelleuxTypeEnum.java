package me.aloic.lazybot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MoelleuxTypeEnum
{
    MACARON(0, "Macaron", "#FF3B30"), //马卡龙
    MIRABELLE(30, "Mirabelle", "#FF9500"), //黄香李塔
    FINANCIER(60, "Financier", "#FFCC00"), //	费南雪金砖蛋糕
    PISTACHE(90, "Pistache", "#A2E300"), //开心果慕斯
    MENTHE(120, "Menthe", "#28CD41"), //薄荷巧克力
    VERVEINE(150, "Verveine", "#00C78C"), //马鞭草奶酱
    AZUR(180, "Azur", "#00D9CC"), //蔚蓝海盐焦糖
    MYRTILLE(210, "Myrtille", "#0A84FF"), //蓝莓克拉芙缇
    LAVANDE(240, "Lavande", "#0039E6"), //薰衣草蜜糖
    VIOLETTE(270, "Violette", "#5E5CE6"), //紫罗兰糖花
    CHATAIGNE(300, "Chataigne", "#BF5AF2"), //栗子蒙布朗
    FRAMBOISE(330, "Framboise", "#FF375F"), //树莓歌剧院蛋糕
    NOIR(361, "Noir", "#000000");  // 黑巧克力蛋糕

    private final int hue;
    private final String name;
    private final String hex;

    public static MoelleuxTypeEnum fromHue(int inputHue) {
        if (inputHue == 361) {
            return NOIR;
        }
        int normalizedHue = ((inputHue % 360) + 360) % 360;
        int baseHue = (normalizedHue / 30) * 30;
        if (baseHue == 360) baseHue = 0;
        for (MoelleuxTypeEnum dessert : values()) {
            if (dessert.hue == baseHue) {
                return dessert;
            }
        }
        return null;
    }
}
