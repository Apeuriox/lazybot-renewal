package me.aloic.lazybot.util;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 右优先的参数解析器, 用于将QQ指令的字符串参数列表解析为具体命令需要的参数.
 *
 * 核心规则: 从参数列表末尾向右匹配, 匹配成功则消费该参数.
 * 最终未被消费的参数 join 起来成为 playerName.
 */
public class ArgumentParser
{
    private final Deque<String> args;

    private ArgumentParser(List<String> args)
    {
        this.args = new ArrayDeque<>(args != null ? args : List.of());
    }

    public static ArgumentParser of(List<String> args)
    {
        return new ArgumentParser(args);
    }

    /** 防御性复制当前未消费的参数 */
    public List<String> remaining()
    {
        return List.copyOf(args);
    }

    public boolean isEmpty()
    {
        return args.isEmpty();
    }

    // ======================== tryPop ========================

    /**
     * 用正则匹配最后一个参数. 成功则弹出并消费, 失败则参数留回队列.
     *
     * @return this 用于链式调用
     */
    public ArgumentParser tryPop(Pattern pattern, Consumer<Matcher> handler)
    {
        if (!args.isEmpty())
        {
            Matcher m = pattern.matcher(args.peekLast());
            if (m.matches())
            {
                args.removeLast();
                handler.accept(m);
            }
        }
        return this;
    }

    /**
     * 和 tryPop 一样, 但附加条件: 正则匹配 + 条件满足 才消费参数.
     * 条件不满足时参数留在队列中——解决 "100 是 index 还是 playerName" 的歧义.
     *
     * @param condition 匹配成功后还需满足的条件判断
     * @return this 用于链式调用
     */
    public ArgumentParser tryPopIf(Pattern pattern, Predicate<Matcher> condition, Consumer<Matcher> handler)
    {
        if (!args.isEmpty())
        {
            Matcher m = pattern.matcher(args.peekLast());
            if (m.matches() && condition.test(m))
            {
                args.removeLast();
                handler.accept(m);
            }
        }
        return this;
    }

    /**
     * tryPop 的 BiConsumer 版本, handler 同时拿到 Matcher 和 this, 用于需要再查看剩余参数的场景.
     */
    public ArgumentParser tryPop(Pattern pattern, BiConsumer<Matcher, ArgumentParser> handler)
    {
        if (!args.isEmpty())
        {
            Matcher m = pattern.matcher(args.peekLast());
            if (m.matches())
            {
                args.removeLast();
                handler.accept(m, this);
            }
        }
        return this;
    }

    /**
     * tryPopIf 的 BiConsumer 版本.
     */
    public ArgumentParser tryPopIf(Pattern pattern, Predicate<Matcher> condition, BiConsumer<Matcher, ArgumentParser> handler)
    {
        if (!args.isEmpty())
        {
            Matcher m = pattern.matcher(args.peekLast());
            if (m.matches() && condition.test(m))
            {
                args.removeLast();
                handler.accept(m, this);
            }
        }
        return this;
    }

    // ======================== 批量匹配 ========================

    /**
     * 对队列末尾批量尝试匹配, 直到匹配失败为止.
     * 适用于多个同类型参数排列在末尾的场景.
     */
    public ArgumentParser tryPopAll(Pattern pattern, Consumer<Matcher> handler)
    {
        while (!args.isEmpty())
        {
            Matcher m = pattern.matcher(args.peekLast());
            if (m.matches())
            {
                args.removeLast();
                handler.accept(m);
            }
            else
            {
                break;
            }
        }
        return this;
    }

    // ======================== 剩余 ========================

    /**
     * 将所有剩余参数用空格 join 起来返回 (即 playerName).
     */
    public String remainder()
    {
        return String.join(" ", args).trim();
    }

    /**
     * 跳过指定数量的参数, 将剩余 join 起来.
     */
    public String remainderSkipping(int skipCount)
    {
        List<String> list = new ArrayList<>(args);
        if (skipCount >= list.size())
            return "";
        return String.join(" ", list.subList(skipCount, list.size())).trim();
    }
}
