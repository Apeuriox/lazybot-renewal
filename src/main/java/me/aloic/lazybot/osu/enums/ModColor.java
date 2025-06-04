package me.aloic.lazybot.osu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.Mod;
import me.aloic.lazybot.osu.theme.Color.HSL;

@Getter
@AllArgsConstructor
public enum ModColor
{
    HR(new HSL(346, 100, 27),
            new HSL(346, 55, 41),
            new HSL(347, 72, 33),
            new HSL(50, 100, 53)),

    HD(new HSL(52, 100, 37),
            new HSL(49, 58, 59),
            new HSL(51, 64, 48),
            new HSL(50, 100, 53)),

    DT(new HSL(251, 62, 38),
            new HSL(252, 51, 45),
            new HSL(250, 51, 41),
            new HSL(50, 100, 53)),

    FL(new HSL(120, 100, 0),
            new HSL(0, 0, 19),
            new HSL(330, 4, 10),
            new HSL(50, 100, 53)),

    BL(new HSL(174, 48, 38),
            new HSL(173, 26, 49),
            new HSL(170, 22, 58),
            new HSL(50, 100, 53)),

    SD(new HSL(28, 49, 31),
            new HSL(29, 32, 44),
            new HSL(25, 42, 38),
            new HSL(50, 100, 53)),

    NC(new HSL(277, 82, 52),
            new HSL(281, 100, 57),
            new HSL(283, 87, 53),
            new HSL(50, 100, 53)),

    PF(new HSL(30, 75, 60),
            new HSL(30, 71, 65),
            new HSL(29, 80, 65),
            new HSL(50, 100, 53)),



    EZ(new HSL(148, 89, 29),
            new HSL(151, 50, 44),
            new HSL(148, 70, 36),
            new HSL(89, 89, 86)),

    NF(new HSL(199, 100, 32),
            new HSL(199, 65, 39),
            new HSL(198, 78, 38),
            new HSL(89, 89, 86)),

    HT(new HSL(240, 9, 22),
            new HSL(202, 4, 35),
            new HSL(0, 0, 28),
            new HSL(89, 89, 86)),

    DC(new HSL(97, 21, 52),
            new HSL(92, 22, 56),
            new HSL(89, 22, 60),
            new HSL(89, 89, 86)),



    TD(new HSL(198, 77, 52),
            new HSL(199, 83, 62),
            new HSL(198, 74, 57),
            new HSL(207, 100, 70)),

    SO(new HSL(314, 93, 12),
            new HSL(308, 31, 27),
            new HSL(317, 54, 21),
            new HSL(207, 100, 70)),

    RX(TD.detailedPrimaryColor,TD.detailedSecondaryColor,TD.detailedSideColor,TD.typeColor),

    AP(TD.detailedPrimaryColor,TD.detailedSecondaryColor,TD.detailedSideColor,TD.typeColor),



    CL(new HSL(246, 30, 53),
            new HSL(248, 30, 56),
            new HSL(249, 30, 59),
            new HSL(263, 100, 70)),

    SG(CL.detailedPrimaryColor,CL.detailedSecondaryColor,CL.detailedSideColor,CL.typeColor),
    AL(CL.detailedPrimaryColor,CL.detailedSecondaryColor,CL.detailedSideColor,CL.typeColor),



    MU(new HSL(246, 30, 53),
            new HSL(248, 30, 56),
            new HSL(249, 30, 59),
            new HSL(263, 100, 70)),

    DA(new HSL(23, 24, 52),
            new HSL(24, 25, 56),
            new HSL(25, 25, 60),
            new HSL(263, 100, 70)),

    MR(new HSL(100, 100, 12),
            new HSL(100, 52, 21),
            new HSL(100, 35, 29),
            new HSL(263, 100, 70)),




    TC(new HSL(26, 72, 43),
            new HSL(27, 56, 49),
            new HSL(29, 56, 56),
            new HSL(342, 100, 78)),

    DEFAULT(new HSL(333, 76, 65),
            new HSL(333, 76, 69),
            new HSL(333, 75, 73),
            new HSL(342, 100, 78));

    public final HSL detailedPrimaryColor;
    public final HSL detailedSecondaryColor;
    public final HSL detailedSideColor;
    public final HSL typeColor;



    public static ModColor fromString(String mod) {
        try {
            return ModColor.valueOf(mod);
        } catch (IllegalArgumentException e) {
            return DEFAULT;
        }
    }
    public static String getModTypeColorHEX(org.spring.osu.model.Mod mod) {
        return getModTypeColorHEX(mod.getType());
    }
    public static String getModTypeColorHEX(Mod mod) {
        return getModTypeColorHEX(mod.getAcronym());
    }
    public static String getModTypeColorHEX(String mod) {
        if (mod==null) throw new LazybotRuntimeException("Mod内容无效");
        return switch (mod.trim().toUpperCase()) {
            case "HD","HR","FL","NC","DT","SD","PF","BL","ST","AC" -> "#ffd810";
            case "EZ","HT","NF","DC" -> "#ddfbbc";
            case "SO","RX","AP","TD" -> "#64baff";
            case "CL","DA","TP","RD","MR","AL","SG" -> "#a066ff";
            default -> "#ff8fb1";
        };
    }

}
