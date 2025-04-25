package me.aloic.lazybot.util;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class VirtualThreadExecutorHolder {
    public static final Executor VIRTUAL_EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();
}
