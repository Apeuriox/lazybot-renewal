package me.aloic.lazybot.util;

import me.aloic.lazybot.exception.LazybotRuntimeException;

import java.util.Map;

public class AuthorityVerifier
{
    private static final Map<Long,Boolean> adminMap;  //Long ass numbers are discord ids

    static{
        adminMap = Map.of( 1524185356L,true,
                412246007024451585L,true,
                1204694006L,true);
    }

    public static void isAdmin(long id)
    {
        if(!adminMap.containsKey(id)) throw new LazybotRuntimeException("权限不足");
    }

}
