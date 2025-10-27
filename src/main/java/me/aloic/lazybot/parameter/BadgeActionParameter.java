package me.aloic.lazybot.parameter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class BadgeActionParameter extends LazybotCommandParameter
{
    private String name;
    private String desc;
    private String altName;
    private String type;


    @Override
    public void validateParams()
    {

    }

    public static BadgeActionParameter analyzeParameter(List<String> params)
    {
        BadgeActionParameter parameter=new BadgeActionParameter();
        if (!params.isEmpty())
        {
            String input = String.join(" ", params);
            // Regex pattern to match content inside curly braces
            Pattern pattern = Pattern.compile("\\{([^=]+)=([^}]*)\\}");
            Matcher matcher = pattern.matcher(input);
            while (matcher.find()) {
                switch (matcher.group(1).trim().toLowerCase())
                {
                    case "name" -> parameter.setName(matcher.group(2).trim());
                    case "desc" -> parameter.setDesc(matcher.group(2).trim());
                    case "alt" -> parameter.setAltName(matcher.group(2).trim());
                    case "type" -> parameter.setType(matcher.group(2).trim());
                }
            }
        }
        else throw new LazybotRuntimeException("输入参数为空");
        return parameter;
    }
    public static void setupDefaultValue(BadgeActionParameter parameter, AccessTokenPO accessTokenPO)
    {
    }

}
