package me.aloic.lazybot.util;

import java.util.regex.Pattern;

/**
 * ArgumentParser 的配套常量类, 存放预编译的正则 Pattern.
 * 所有命令共享同一份 Pattern 实例, 避免每次请求重复编译.
 */
public final class Parsers
{
    /** "4889657+HDHR" → group(1)=bid, group(2)=mod */
    public static final Pattern BID_PLUS_MOD_NO_SPACE = Pattern.compile("^(\\d{1,10})\\+([a-zA-Z]{2,})$");

    /** "+HDDT" → group(1)=mod */
    public static final Pattern MOD = Pattern.compile("^\\+([a-zA-Z]{2,})$");

    /** 纯数字字符串 */
    public static final Pattern DIGITS = Pattern.compile("^\\d{1,10}$");

    /** "#3" → group(1)=index */
    public static final Pattern INDEX = Pattern.compile("^#(\\d+)$");

    /** "1-100" → group(1)=from, group(2)=to */
    public static final Pattern RANGE = Pattern.compile("^(\\d+)-(\\d+)$");

    /** 整数或小数, 如 "96" 或 "98.5" — 用于范围有限的数值参数 (如 accuracy 0-100) */
    public static final Pattern NUMBER = Pattern.compile("^\\d+(\\.\\d+)?$");

    /** "AR9.5" → group(1)=9.5 — AR override combined format */
    public static final Pattern AR_COMBINED = Pattern.compile("^(?i)AR(\\d+(\\.\\d+)?)$");

    /** "AR" → standalone AR prefix, number follows as next arg */
    public static final Pattern AR_PREFIX = Pattern.compile("^(?i)AR$");

    private Parsers() {}
}
