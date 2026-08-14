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

    /** BpIf mod operation, e.g. +HDHR, -HD, or !HDDT. */
    public static final Pattern BPIF_MOD_OPERATION = Pattern.compile("^[+\\-!！][a-zA-Z]*!?$");

    /** 整数或小数, 如 "96" 或 "98.5" — 用于范围有限的数值参数 (如 accuracy 0-100) */
    public static final Pattern NUMBER = Pattern.compile("^\\d+(\\.\\d+)?$");

    /** "AR9.5", "CS4", "OD8" → group(1)=attribute, group(2)=value. */
    public static final Pattern DIFFICULTY_OVERRIDE = Pattern.compile(
            "(?i)^(AR|CS|OD)(\\d+(?:\\.\\d+)?)$");

    /** Standalone difficulty attribute prefix; a numeric value follows as the next argument. */
    public static final Pattern DIFFICULTY_OVERRIDE_PREFIX = Pattern.compile("(?i)^(AR|CS|OD)$");

    private Parsers() {}
}
