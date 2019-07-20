package com.gentcent.wechat.zzk.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class ThreadPoolUtils {
    private ExecutorService exe;
    private static ThreadPoolUtils b;

    private ThreadPoolUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static synchronized ThreadPoolUtils getInstance() {
        ThreadPoolUtils mVar;
        synchronized (ThreadPoolUtils.class) {
            if (b == null) {
                b = new ThreadPoolUtils(0, 15);
            }
            mVar = b;
        }
        return mVar;
    }

    private ThreadPoolUtils(int i, int i2) {
        switch (i) {
            case 0:
                this.exe = Executors.newScheduledThreadPool(i2);
                return;
            case 1:
                this.exe = Executors.newFixedThreadPool(i2);
                return;
            case 2:
                this.exe = Executors.newCachedThreadPool();
                return;
            case 3:
                this.exe = Executors.newSingleThreadExecutor();
                return;
            default:
                return;
        }
    }

    public void run(Runnable runnable) {
        this.exe.execute(runnable);
    }

    public ScheduledFuture<?> a(Runnable runnable, long j, TimeUnit timeUnit) {
        ExecutorService executorService = this.exe;
        if (executorService instanceof ScheduledExecutorService) {
            return ((ScheduledExecutorService) executorService).schedule(runnable, j, timeUnit);
        }
        throw new ClassCastException("Exec can't cast to ScheduledExecutorService.");
    }

    public ScheduledFuture<?> a(Runnable runnable, long j, long j2, TimeUnit timeUnit) {
        ExecutorService executorService = this.exe;
        if (executorService instanceof ScheduledExecutorService) {
            return ((ScheduledExecutorService) executorService).scheduleWithFixedDelay(runnable, j, j2, timeUnit);
        }
        throw new ClassCastException("Exec can't cast to ScheduledExecutorService.");
    }
}
