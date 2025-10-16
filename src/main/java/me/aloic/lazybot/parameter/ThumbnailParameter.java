package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.graphics.mapping.documentMapper.ThumbnailSVGMapper;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;

import javax.naming.ldap.PagedResultsControl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThumbnailParameter extends LazybotCommandParameter
{
    private Integer index;
    private String comment;
    private Integer beatmapId;
    private Integer version;
    private Integer position;
    private List<ThumbnailSVGMapper.ThumbnailClassicalAttribute> attributes;

    public ThumbnailParameter(String comment, Integer beatmapId, String mode, Integer version, String playerName)
    {
        this.comment=comment;
        this.setMode(mode);
        this.beatmapId=beatmapId;
        this.version=version;
        this.setPlayerName(playerName);
    }

    @Override
    public void validateParams() {
        if(version==null) {
            version=0;
        }
        if (index==null) {
            index=1;
        }
        if (index<0 || index>50)
        {
            throw new LazybotRuntimeException("索引超出范围");
        }

    }
    public static ThumbnailParameter analyzeParameter(List<String> params)
    {
        ThumbnailParameter result=new ThumbnailParameter();
        if (!params.isEmpty())
        {
            String input = String.join(" ", params);
            // Regex pattern to match content inside curly braces
            Pattern pattern = Pattern.compile("\\{([^=]+)=([^}]*)\\}");
            Matcher matcher = pattern.matcher(input);
            Map<String, String> extracted = new HashMap<>();
            while (matcher.find()) {
                extracted.put(matcher.group(1).trim(),matcher.group(2).trim());
            }
            if (extracted.get("id")!=null && extracted.get("id").matches("\\d+")) {
                result.beatmapId = Integer.parseInt(extracted.get("id"));
            }
            if (extracted.get("u")!=null) {
                result.setPlayerName(extracted.get("u"));
            }
            if (extracted.get("c")!=null) {
                result.setComment(extracted.get("c"));
            }
            if (extracted.get("attr")!=null) {
                result.setAttributes(ThumbnailSVGMapper.ThumbnailClassicalAttribute.parseAttribute(extracted.get("attr")));
            }
            if (extracted.get("i")!=null && extracted.get("i").matches("\\d+")) {
                result.setIndex(Integer.parseInt(extracted.get("i")));
            }
            if (extracted.get("p")!=null && extracted.get("p").matches("\\d+")) {
                int po= Integer.parseInt(extracted.get("p"));
                if (po<0)
                {
                    throw new LazybotRuntimeException("位次不合法");
                }
                result.setPosition(po);
            }
        }
        return result;
    }
    public static void setupDefaultValue(ThumbnailParameter scoreParameter, AccessTokenPO accessTokenPO)
    {
        scoreParameter.setPlayerId(accessTokenPO.getPlayer_id());
        if (scoreParameter.getMode() == null)
            scoreParameter.setMode(accessTokenPO.getDefault_mode());
        if (scoreParameter.getVersion() == null)
            scoreParameter.setVersion(0);

    }

}
