package me.aloic.lazybot.shiro.event;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.command.CommandReply;
import me.aloic.lazybot.osu.enums.IdentityPlatform;
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
   private List<String> atParameters;
   //only for test use
   private String commandString;
   private IdentityPlatform identityPlatform;
   private String platformUserId;
   private String platformChannelId;
   private String sourceMessageId;
   private CommandReply reply;

   public LazybotSlashCommandEvent(GroupMessageEvent event) {
      this.messageEvent = event;
      this.istSlashCommand = false;
      this.scorePanelVersion=1;
   }
   public LazybotSlashCommandEvent(String command) {
      this.commandString = command;
      this.istSlashCommand=true;
   }

   @Override
   public String toString()
   {
      return "LazybotSlashCommandEvent{" +
              "istSlashCommand=" + istSlashCommand +
              ", commandType='" + commandType + '\'' +
              ", commandParameters=" + commandParameters +
              ", messageEvent=" + messageEvent +
              ", scorePanelVersion=" + scorePanelVersion +
              ", osuMode=" + osuMode +
              ", atParameters=" + atParameters +
              ", commandString='" + commandString + '\'' +
              '}';
   }
}
