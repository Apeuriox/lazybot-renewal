package me.aloic.lazybot.parameter;

import lombok.Data;
import me.aloic.lazybot.osu.dao.entity.po.UserTokenPO;

@Data
public abstract class LazybotCommandParameter
{
    private UserTokenPO accessToken;
}