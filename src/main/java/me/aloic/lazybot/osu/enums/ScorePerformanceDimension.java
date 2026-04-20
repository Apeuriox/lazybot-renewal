package me.aloic.lazybot.osu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.aloic.lazybot.exception.LazybotRuntimeException;

@Getter
@AllArgsConstructor
public enum ScorePerformanceDimension
{
    ALL("pp", "Total PP"),
    AIM( "aim", "Aim (Including Jump and Flow)"),
    JUMP("jump","Acute Aim (Jump)"),
    FLOW("flow", "Stream Flow"),
    SPEED("speed", "SSG Power"),
    STAMINA( "stamina", "Stamina"),
    ACCURACY("accuracy", "Accuracy"),
    PRECISION( "precision", "Precision");

    private final String field;
    private final String showcase;


    public static ScorePerformanceDimension getDimension(String input) {
        if (input == null) return ALL;
        return switch (input.toLowerCase().trim())
        {
            case "pp", "all", "full","total pp" -> ALL;
            case "aim" -> AIM;
            case "jump" -> JUMP;
            case "flow", "stream", "worm" -> FLOW;
            case "speed", "spd", "ssg" -> SPEED;
            case "stamina", "sta" -> STAMINA;
            case "accuracy", "acc" -> ACCURACY;
            case "precision", "pre" -> PRECISION;
            default -> throw new LazybotRuntimeException("无效的次级类型: " + input);
        };
    }


}
