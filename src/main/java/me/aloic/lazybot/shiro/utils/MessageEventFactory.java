package me.aloic.lazybot.shiro.utils;

import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.monitor.ResourceMonitor;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class MessageEventFactory
{
    @Value("${lazybot.prefix}")
    private String commandPrefix;

    private static final Map<String, OsuMode> modeMap;

    private static final List<String> NON_OSU_COMMAND;

    private static final Logger logger = LoggerFactory.getLogger(MessageEventFactory.class);
    static{
        modeMap =  Map.of(
                ":0",OsuMode.Osu,
                ":1",OsuMode.Taiko,
                ":2",OsuMode.Catch,
                ":3",OsuMode.Mania);

        NON_OSU_COMMAND = List.of("help", "customize","addtips","tips");
    }


    public LazybotSlashCommandEvent setupSlashCommandEvent(GroupMessageEvent event)
    {
        LazybotSlashCommandEvent slashCommandEvent = new LazybotSlashCommandEvent(event);
        try{
            if (event.getMessage().startsWith(commandPrefix)) {
                slashCommandEvent.setIstSlashCommand(true);
                analyzeCommand(slashCommandEvent);
            }
            return slashCommandEvent;
        }
        catch (Exception e){
            logger.error("解析参数时出错",e);
            throw new LazybotRuntimeException("解析参数时出错");
        }

    }

    private static void analyzeCommand(LazybotSlashCommandEvent slashCommandEvent)
    {
        String s = convertString(slashCommandEvent.getMessageEvent().getMessage());
        s=s.substring(1);
        List<String> information = new java.util.ArrayList<>(List.of(s.split(" ")));
        if (!NON_OSU_COMMAND.contains(information.getFirst().toLowerCase().trim())) {
            s = formatCommand(s);
            slashCommandEvent.setScorePanelVersion(countOccurrences(s, '&'));
            s = s.replace("&", "");

            information = new java.util.ArrayList<>(List.of(s.split(" ")));
            for (String str : information)
            {
                if (str.startsWith(":"))
                {
                    if (modeMap.containsKey(str))
                    {
                        slashCommandEvent.setOsuMode(modeMap.get(str));
                        information.remove(str);
                        break;
                    }
                    else information.remove(str);
                }
            }
        }
        slashCommandEvent.setCommandType(information.getFirst());
        slashCommandEvent.setCommandParameters(information.subList(1, information.size()));
    }
    private static String formatCommand(String s)
    {
        s = s.replace("！","!").trim();
        s = s.replace("：",":");
        StringBuffer sb = new StringBuffer(s);
        for (int i = 0; i < sb.length(); i++) {
            if((sb.charAt(i) == ':' && sb.charAt(i - 1) != ' ')||(sb.charAt(i) == '&' && sb.charAt(i - 1) != ' ')){
                sb.insert(i, ' ');
                i++;
            }
            else if(sb.charAt(i) == ' ' && sb.charAt(i + 1) ==' '){
                sb.deleteCharAt(i);
                i--;
            }
        }
        return sb.toString().trim().toLowerCase();
    }
    public static String convertString(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input
                .replace("&#91;", "[")
                .replace("&#93;", "]")
                .replace("&amp;", "&")
                .replace("&#44;", ",");
    }
    public static int countOccurrences(String originalStr, char target) {
        int count = 0;
        for (char c : originalStr.toCharArray()) {
            if (c == target) {
                count++;
            }
        }
        return count;
    }

}
