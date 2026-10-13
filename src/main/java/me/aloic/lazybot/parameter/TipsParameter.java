package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.util.CommonTool;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class TipsParameter extends LazybotCommandParameter
{
    private Integer id;
    private String query;

    @Override
    public void validateParams()
    {
        if (id == null) id = 0;
        if (id < 0) throw new LazybotRuntimeException("{id} 必须大于 0");
        if (id > 0) {
            query = null;
            return;
        }
        if (query == null || query.isBlank()) {
            query = null;
            return;
        }
        query = query.trim().replaceAll("\\s+", " ");
        for (String token : query.split(" ")) {
            if (token.codePointCount(0, token.length()) < 2 && !isSingleCjk(token)) {
                throw new LazybotRuntimeException("搜索关键词过短");
            }
        }
    }

    public TipsParameter(Integer id) {
        this.id = id;
    }

    public static TipsParameter analyzeParameter(List<String> params)
    {
        TipsParameter parameter = new TipsParameter();
        if (!params.isEmpty()) {
            if (params.size() == 1 && CommonTool.isPositiveInteger(params.getFirst())) {
                parameter.setId(Integer.valueOf(params.getFirst()));
            }
            else {
                if (CommonTool.isPositiveInteger(params.getFirst())) parameter.setId(Integer.valueOf(params.getFirst()));
            }
        }
        return parameter;
    }

    public static TipsParameter analyzeTipsParameter(List<String> params)
    {
        TipsParameter parameter = new TipsParameter();
        if (params == null || params.isEmpty()) {
            return parameter;
        }
        if (params.size() == 1 && CommonTool.isPositiveInteger(params.getFirst())) {
            parameter.setId(Integer.valueOf(params.getFirst()));
            return parameter;
        }
        parameter.setQuery(String.join(" ", params));
        return parameter;
    }

    private static boolean isSingleCjk(String token)
    {
        if (token.codePointCount(0, token.length()) != 1) {
            return false;
        }
        Character.UnicodeScript script = Character.UnicodeScript.of(token.codePointAt(0));
        return script == Character.UnicodeScript.HAN
                || script == Character.UnicodeScript.HIRAGANA
                || script == Character.UnicodeScript.KATAKANA
                || script == Character.UnicodeScript.HANGUL;
    }
}
