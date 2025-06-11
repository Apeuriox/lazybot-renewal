package me.aloic.lazybot.osu.dao.entity.dto.lazybot;

import lombok.Data;

@Data
public class LazybotWebResult<T>
{
    private Integer code;
    private T data;
    private String msg;
}
