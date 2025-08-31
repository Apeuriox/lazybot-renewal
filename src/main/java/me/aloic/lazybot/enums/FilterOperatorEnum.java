package me.aloic.lazybot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FilterOperatorEnum
{
    GT(">"),
    GTE(">="),
    LT("<"),
    LTE("<="),
    EQ("=="),
    NE("!="),
    CT("="),
    SW("^="),
    EW("$="),
    LIKE("~");

    private final String symbol;

    public static FilterOperatorEnum fromSymbol(String s) {
        for (FilterOperatorEnum op : values()) {
            if (op.symbol.equals(s)) {
                return op;
            }
        }
        throw new IllegalArgumentException("[Lazybot] 未知运算符: " + s);
    }
}
