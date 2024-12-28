package me.aloic.lazybot.osu.dao.entity.dto.player;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class UserGroupDTO extends GroupDTO implements Serializable{
    private String[] playmodes;
}
