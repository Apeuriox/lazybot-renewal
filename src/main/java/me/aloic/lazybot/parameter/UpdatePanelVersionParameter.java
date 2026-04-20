package me.aloic.lazybot.parameter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class UpdatePanelVersionParameter extends GeneralParameter
{
   private Long qqCode;

   public UpdatePanelVersionParameter(GeneralParameter p)
   {
      this.setPlayerName(p.getPlayerName());
   }

}
