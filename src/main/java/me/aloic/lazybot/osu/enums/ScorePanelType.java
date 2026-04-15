package me.aloic.lazybot.osu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum ScorePanelType
{
    Dark(null,"暗色模式", "默认样式，支持四模式"),
    White(1, "亮色模式","最初版废案"),
    Material(2,"Material","废案，以Material 3风格设计，支持不佳不建议使用"),
    QuadraGrid(3,"Quadra Grid","为PP+单独设计，包含PP+数据，仅限osu模式"),
    Marathon(4,"Marathon","2026年愚人节特供，高饱和酸性艺术设计，仅限osu模式");

    private final Integer internalVersionCode;
    private final String fullName;
    private final String describe;




}
