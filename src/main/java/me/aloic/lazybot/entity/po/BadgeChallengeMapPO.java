package me.aloic.lazybot.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.util.CommonTool;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName(value = "badge_challenge_map", autoResultMap = true)
public class BadgeChallengeMapPO implements Serializable
{
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer challenge_id;
    private Integer beatmap_id;
    private Double required_acc;
    private Integer required_combo;
    private Integer max_accepted_miss;
    private String mods_allowed;
    private Integer mode;
    private String title_with_version;

    public String toLazybotString()
    {
        StringBuilder sb=new StringBuilder(title_with_version);
        sb.append(" (").append(beatmap_id).append(") ").append("\n");
        sb.append("准确率要求: >=").append(CommonTool.toString(required_acc*100.0)).append("% ").append("\n");
        sb.append("Combo要求: >=").append(required_combo).append(" ").append("\n");
        sb.append("允许Miss数: <=").append(max_accepted_miss).append(" ").append("\n");
        sb.append("Mod限制: ").append(inlineMods(mods_allowed)).append("\n");
        sb.append("\n");
        return sb.toString();
    }
    private String inlineMods(String mods)
    {
        if (mods_allowed==null) return "仅允许Nomod";
        char op = mods.charAt(0);
        String modPattern = (op == '=' || op == '~' || op == '!' || op == '^')
                ? mods.substring(1)
                : mods;
        StringBuilder sb=new StringBuilder();
        switch (op)
        {
            case '=' -> sb.append("严格匹配 ").append(addSpaceSimple(modPattern));
            case '~' -> sb.append("必须包含 ").append(addSpaceSimple(modPattern));
            case '!' -> sb.append("不允许出现 ").append(addSpaceSimple(modPattern));
            case '^' -> sb.append("允许 ").append(addSpaceSimple(modPattern));
            default -> sb.append("包含 ").append(addSpaceSimple(modPattern));
        };
        return sb.toString();
    }
    public static String addSpaceSimple(String input) {
        // Handle null or empty strings
        if (input == null || input.isEmpty()) {
            return input;
        }

        // Split the string into chunks of 2 characters.
        // The regex "(?<=\\G.{2})" is a "lookbehind" that matches the position
        // after every two characters.
        // \\G is the end of the previous match.
        String[] chunks = input.split("(?<=\\G.{2})");

        // Join the chunks with a space in between.
        return String.join(" ", chunks);
    }



}
