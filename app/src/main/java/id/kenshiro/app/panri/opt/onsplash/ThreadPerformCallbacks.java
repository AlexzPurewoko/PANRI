package id.kenshiro.app.panri.opt.onsplash;

import org.jetbrains.annotations.NotNull;

public interface ThreadPerformCallbacks {
    public void onStarting(@NotNull Runnable runnedThread);

    public void onCompleted(@NotNull Runnable runnedThread, @NotNull Object returnedCallbacks);

    public void onCancelled(@NotNull Runnable runnedThread, @NotNull Throwable caused, @NotNull Object returnedCallbacks);
}
