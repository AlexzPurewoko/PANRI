package id.kenshiro.app.panri.opt.checkupdates;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import id.kenshiro.app.panri.important.KeyListClasses;
import id.kenshiro.app.panri.opt.onsplash.ThreadPerformCallbacks;

public class CheckDBUpdaterService extends Service {
    Thread remoteUpdaterService;
    Thread checkDBUpdaterService;
    Thread updater;
    String[] fetchedVersion = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        remoteUpdaterService = new Thread(new RemoteUpdaterService(this), "RemotePANRIUpdaterService");
        remoteUpdaterService.start();
        checkDBUpdaterService = new Thread(new CheckDBCloudThread(this, new ThreadPerformCallbacks() {
            @Override
            public void onStarting(@NotNull Runnable runnedThread) {

            }

            @Override
            public void onCompleted(@NotNull Runnable runnedThread, @NotNull Object returnedCallbacks) {
                fetchedVersion = (String[]) returnedCallbacks;
                setTheVersion(fetchedVersion);
                // stop thread and any
                stopThis();
            }

            @Override
            public void onRunning(@NotNull Runnable runnedThread, @NotNull Object returnedCallbacks) {

            }

            @Override
            public void onCancelled(@NotNull Runnable runnedThread, @NotNull Throwable caused, @NotNull Object returnedCallbacks) {

            }
        }));
        checkDBUpdaterService.start();
    }

    private void stopThis() {
        if (remoteUpdaterService != null && remoteUpdaterService.isAlive())
            remoteUpdaterService.interrupt();
        stopSelf();
    }

    @Override
    public void onDestroy() {
        if (checkDBUpdaterService != null)
            if (checkDBUpdaterService.isAlive())
                checkDBUpdaterService.interrupt();
        super.onDestroy();
    }

    private void setTheVersion(String[] fetchedVersion) {
        int app_version = Integer.parseInt(fetchedVersion[0]);
        int cloud_version = Integer.parseInt(fetchedVersion[1]);
        if (cloud_version > app_version)
            setOnShareds(KeyListClasses.DB_REQUEST_UPDATE, fetchedVersion);
        else if (app_version == cloud_version)
            setOnShareds(KeyListClasses.DB_IS_SAME_VERSION, fetchedVersion);

    }

    private void setOnShareds(int dbIsNewerVersion, String[] fetchedVersion) {
        SharedPreferences sharedPreferences = getSharedPreferences(KeyListClasses.SHARED_PREF_NAME, MODE_PRIVATE);
        sharedPreferences.edit().putString(KeyListClasses.KEY_VERSION_ON_CLOUD, fetchedVersion[1]).commit();
        sharedPreferences.edit().putInt(KeyListClasses.KEY_VERSION_BOOL_NEW, dbIsNewerVersion).commit();
    }

    private class Updater implements Runnable {
        CheckDBUpdaterService service;
        CheckDBCloudThread checkDBCloudThread;
        ThreadPerformCallbacks callbacks;

        Updater(CheckDBUpdaterService service, ThreadPerformCallbacks callbacks) {
            this.service = service;
            this.callbacks = callbacks;
        }

        @Override
        public void run() {
            checkDBCloudThread = new CheckDBCloudThread(service, callbacks);
            checkDBCloudThread.run();
        }
    }
}
