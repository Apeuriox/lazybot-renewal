package me.aloic.lazybot.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class CommandStat {
    private final String commandName;
    private final AtomicInteger callCount = new AtomicInteger(0);
    private final List<CommandCallRecord> callRecords = Collections.synchronizedList(new ArrayList<>());

    public CommandStat(String commandName) {
        this.commandName = commandName;
    }

    public void recordCall(String user, String channel) {
        callCount.incrementAndGet();
        callRecords.add(new CommandCallRecord(user, channel, Instant.now()));
    }

    public int getCallCount() {
        return callCount.get();
    }

    public List<CommandCallRecord> getCallRecords() {
        return new ArrayList<>(callRecords);
    }
}

