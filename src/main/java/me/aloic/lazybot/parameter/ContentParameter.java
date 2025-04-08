package me.aloic.lazybot.parameter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.exception.LazybotRuntimeException;

import java.util.List;
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ContentParameter extends LazybotCommandParameter
{
    private String content;
    private Long userIdentity;

    @Override
    public void validateParams()
    {
        if (content==null|| content.trim().isEmpty()) throw new LazybotRuntimeException("参数为必选项");
    }
    public ContentParameter(String content) {
        this.content=content;
    }

    public static ContentParameter analyzeParameter(List<String> params)
    {
        ContentParameter parameter=new ContentParameter();
        if (!params.isEmpty()) {
            parameter.setContent(String.join(" ", params));
        }
        return parameter;
    }
}
