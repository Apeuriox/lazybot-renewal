package me.aloic.lazybot.osu.dao.entity.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModIncompatible implements Serializable
{
    private String type;
    private String name;
}
