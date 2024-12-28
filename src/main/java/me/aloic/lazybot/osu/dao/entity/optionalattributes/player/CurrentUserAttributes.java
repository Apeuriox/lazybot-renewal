package me.aloic.lazybot.osu.dao.entity.optionalattributes.player;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
@Data
@AllArgsConstructor
public class CurrentUserAttributes implements Serializable
{
    private String pin;
}
