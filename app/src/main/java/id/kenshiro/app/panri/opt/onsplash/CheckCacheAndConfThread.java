package id.kenshiro.app.panri.opt.onsplash;

import com.mylexz.utils.MylexzActivity;
import com.mylexz.utils.SimpleDiskLruCache;

public class CheckCacheAndConfThread implements Runnable {
    private ThreadPerformCallbacks threadPerformCallbacks;
    private MylexzActivity ctx;
    private SimpleDiskLruCache diskCache;

    public CheckCacheAndConfThread(MylexzActivity ctx, SimpleDiskLruCache diskCache) {
        this.ctx = ctx;
        this.diskCache = diskCache;
    }

    @Override
    public void run() {
        if (threadPerformCallbacks != null)
            threadPerformCallbacks.onStarting(this);
        ConfigureCache configureCache = new ConfigureCache(ctx, diskCache);
        configureCache.configureCache();
        if (threadPerformCallbacks != null)
            threadPerformCallbacks.onCompleted(this, 0);
    }

    public void setThreadPerformCallbacks(ThreadPerformCallbacks threadPerformCallbacks) {
        this.threadPerformCallbacks = threadPerformCallbacks;
    }
}
