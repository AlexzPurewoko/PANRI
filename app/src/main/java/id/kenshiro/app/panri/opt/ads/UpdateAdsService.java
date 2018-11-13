package id.kenshiro.app.panri.opt.ads;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import id.kenshiro.app.panri.important.KeyListClasses;
import id.kenshiro.app.panri.opt.onsplash.ThreadPerformCallbacks;

public class UpdateAdsService extends Service {
    // for controlling the service alived
    Thread remoteService = null;
    // for checking the ads update
    Thread checkAdsUpdate = null;
    // for get collections on databases
    Thread getCollectionsDB = null;
    // for get the ads requested by the main activity thread
    Thread getAdsThread = null;
    // for file lock/unlocking, true if its file is under locking
    AtomicBoolean isIklanFolderLocked = new AtomicBoolean();
    // for store the value of collections
    List<DownloadIklanFiles.DBIklanCollection> collections;
    @Override
    public void onCreate() {
        super.onCreate();
        isIklanFolderLocked.set(false);
        remoteService = new Thread(new RemoteAdsService(this), "RemoteAdsService");
        remoteService.start();
        getCollectionsDB = new Thread(new GetCollectionDBThr(this, new ThreadPerformCallbacks() {
            @Override
            public void onStarting(@NotNull Runnable runnedThread) {

            }

            @Override
            public void onCompleted(@NotNull Runnable runnedThread, @NotNull Object returnedCallbacks) {
                synchronized (collections) {
                    collections = (List<DownloadIklanFiles.DBIklanCollection>) returnedCallbacks;
                }
            }

            @Override
            public void onRunning(@NotNull Runnable runnedThread, @NotNull Object returnedCallbacks) {

            }

            @Override
            public void onCancelled(@NotNull Runnable runnedThread, @NotNull Throwable caused, @NotNull Object returnedCallbacks) {

            }
        }), "GetDatabaseCollectionsProcess");
        getCollectionsDB.start();
        checkAdsUpdate = new Thread(new CheckAdsUpdates(this, new ThreadPerformCallbacks() {
            @Override
            public void onStarting(@NotNull Runnable runnedThread) {

            }

            @Override
            public void onCompleted(@NotNull Runnable runnedThread, @NotNull Object returnedCallbacks) {
                synchronized (collections) {
                    collections = (List<DownloadIklanFiles.DBIklanCollection>) returnedCallbacks;
                }
                // unlock the objects
                synchronized (isIklanFolderLocked) {
                    isIklanFolderLocked.set(false);
                }
                System.gc();
            }

            @Override
            public void onRunning(@NotNull Runnable runnedThread, @NotNull Object returnedCallbacks) {

            }

            @Override
            public void onCancelled(@NotNull Runnable runnedThread, @NotNull Throwable caused, @NotNull Object returnedCallbacks) {

            }
        }), "ServiceDownloadAndCheckIklan");
        checkAdsUpdate.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int request = intent.getIntExtra(KeyListClasses.GET_ADS_MODE_START_SERVICE, 0);
        switch (request) {
            case KeyListClasses.IKLAN_MODE_START_SERVICE:
                Log.i("Service", "Service has been started");
                break;
            case KeyListClasses.IKLAN_MODE_GET_IKLAN: {
                int iklanPosition = intent.getIntExtra(KeyListClasses.NUM_REQUEST_IKLAN_MODES, -1);
                if (iklanPosition >= 0) {
                    GetResultedIklanThr getResultedIklanThr = new GetResultedIklanThr(this, collections, iklanPosition);
                    if (getAdsThread != null) {
                        if (getAdsThread.isAlive()) {
                            // stop thread
                            getAdsThread.interrupt();
                            // unlock
                            synchronized (isIklanFolderLocked) {
                                isIklanFolderLocked.set(false);
                            }
                        }
                        getAdsThread = null;
                    }
                    getAdsThread = new Thread(getResultedIklanThr);
                    getAdsThread.start();
                    System.gc();
                }
            }
            break;
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // check if another thread is alive and interrupt them
        if (checkAdsUpdate != null) {
            if (checkAdsUpdate.isAlive())
                checkAdsUpdate.interrupt();
        }
        if (getAdsThread != null) {
            if (getAdsThread.isAlive())
                getAdsThread.interrupt();
        }
        if (getCollectionsDB != null) {
            if (getCollectionsDB.isAlive())
                getCollectionsDB.interrupt();
        }
        super.onDestroy();
    }
}
