package id.kenshiro.app.panri.opt.onsplash;

import org.jetbrains.annotations.NotNull;

public interface ThreadPerformCallbacks {
    void onStarting(@NotNull Runnable runnedThread);

    void onCompleted(@NotNull Runnable runnedThread, @NotNull Object returnedCallbacks);

    void onRunning(@NotNull Runnable runnedThread, @NotNull Object returnedCallbacks);

    void onCancelled(@NotNull Runnable runnedThread, @NotNull Throwable caused, @NotNull Object returnedCallbacks);
}
