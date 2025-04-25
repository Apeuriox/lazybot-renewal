package me.aloic.lazybot.shiro.event;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.osu.enums.OsuMode;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LazybotSlashCommandEvent
{
   private Boolean istSlashCommand;
   private String commandType;
   private List<String> commandParameters;
   private GroupMessageEvent messageEvent;
   private Integer scorePanelVersion;
   private OsuMode osuMode;
   //only for test use
   private String commandString;

   public LazybotSlashCommandEvent(GroupMessageEvent event) {
      this.messageEvent = event;
      this.istSlashCommand = false;
      this.scorePanelVersion=1;
   }
   public LazybotSlashCommandEvent(String command) {
      this.commandString = command;
      this.istSlashCommand=true;
   }
}
