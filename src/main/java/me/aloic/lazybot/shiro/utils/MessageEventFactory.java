package me.aloic.lazybot.shiro.utils;

import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MessageEventFactory
{
    @Value("${lazybot.prefix}")
    private String commandPrefix;


    public LazybotSlashCommandEvent setupSlashCommandEvent(GroupMessageEvent event)
    {
        LazybotSlashCommandEvent slashCommandEvent = new LazybotSlashCommandEvent(event);
        if (event.getMessage().startsWith(commandPrefix)) {
            slashCommandEvent.setIstSlashCommand(true);
            analyzeCommand(slashCommandEvent);
        }
        return slashCommandEvent;
    }

    private static void analyzeCommand(LazybotSlashCommandEvent slashCommandEvent)
    {
        String s = formatCommand(slashCommandEvent.getMessageEvent().getMessage());
        if(s.contains("&"))
            slashCommandEvent.setScorePanelVersion(0);
        s=s.replace("&", "");

        List<String> information = List.of(s.split(" "));
        slashCommandEvent.setCommandType(information.getFirst());
        slashCommandEvent.setCommandParameters(information.subList(1, information.size()));
    }
    private static String formatCommand(String s)
    {
        s = s.replace("！","!");
        s = s.replace("：",":");
        StringBuffer sb = new StringBuffer(s);
        sb.deleteCharAt(0);
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
}
