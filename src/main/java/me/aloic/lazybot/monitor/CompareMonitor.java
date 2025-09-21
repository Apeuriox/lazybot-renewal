package me.aloic.lazybot.monitor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


@Slf4j
@Getter
public class CompareMonitor
{
    private static final ConcurrentMap<Long, Deque<Integer>> recentBeatmaps = new ConcurrentHashMap<>();
    private static final int MAX_RECENT = 5;

    public static void saveRecentBeatmap(long channelId, int beatmapId) {
        recentBeatmaps.computeIfAbsent(channelId, k -> new ArrayDeque<>());
        Deque<Integer> deque = recentBeatmaps.get(channelId);

        deque.remove(beatmapId);
        if (deque.size() >= MAX_RECENT) {
            deque.removeFirst();
        }
        deque.addLast(beatmapId);
    }

    public static Integer getRecentBeatmap(long channelId, int indexFromLast) {
        Deque<Integer> deque = recentBeatmaps.get(channelId);
        if (deque == null || deque.isEmpty()) {
            throw new LazybotRuntimeException("没有查询到最近的成绩缓存，请先查询一次");
        }
        int size = deque.size();
        if (indexFromLast <= 0 || indexFromLast > size) {
            throw new LazybotRuntimeException("索引超出现缓存上限");
        }
        return deque.toArray(new Integer[0])[size - indexFromLast];
    }

    public void clear() {
        recentBeatmaps.clear();
    }


}
