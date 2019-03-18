package id.kenshiro.app.panri.opt.ads;

import android.content.Context;
import android.content.SharedPreferences;

import org.jetbrains.annotations.NotNull;

import java.net.ConnectException;

import id.kenshiro.app.panri.important.KeyListClasses;
import id.kenshiro.app.panri.opt.CheckConnection;
import id.kenshiro.app.panri.opt.onsplash.ThreadPerformCallbacks;

public class CheckAdsUpdates implements Runnable {
    UpdateAdsService service;
    int cloudVersion = 0;
    Boolean stateThread = false;
    ThreadPerformCallbacks threadPerformCallbacks;

    public CheckAdsUpdates(UpdateAdsService service, ThreadPerformCallbacks threadPerformCallbacks) {
        this.service = service;
        this.threadPerformCallbacks = threadPerformCallbacks;
    }

    @Override
    public void run() {
        boolean isConnected = cekKoneksi();
        if (threadPerformCallbacks != null)
            threadPerformCallbacks.onStarting(this);
        if (isConnected) {
            int currentAds = getCurrentAdsVersion();
            // checks the current version in cloud
            CheckIklanOnCloud checkIklanOnCloud = new CheckIklanOnCloud(currentAds, new ThreadPerformCallbacks() {
                @Override
                public void onStarting(@NotNull Runnable runnedThread) {

                }

                @Override
                public void onCompleted(@NotNull Runnable runnedThread, @NotNull Object returnedCallbacks) {
                    String curr = (String) returnedCallbacks;
                    if (curr.equals("undefined"))
                        cloudVersion = 0;
                    else
                        cloudVersion = Integer.parseInt(curr);
                    synchronized (stateThread) {
                        stateThread = true;
                    }
                }

                @Override
                public void onRunning(@NotNull Runnable runnedThread, @NotNull Object returnedCallbacks) {

                }

                @Override
                public void onCancelled(@NotNull Runnable runnedThread, @NotNull Throwable caused, @NotNull Object returnedCallbacks) {

                }
            });
            Thread checkIklan = new Thread(checkIklanOnCloud, "CheckIklanVersionThread");
            checkIklan.start();

            // wait the current threads
            while (!stateThread) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            // starts the download progress (iklan) if any latest version
            if (cloudVersion > currentAds) {
                stateThread = false;
                DownloadIklanFiles downloadIklanFiles = new DownloadIklanFiles(service, cloudVersion, new ThreadPerformCallbacks() {
                    @Override
                    public void onStarting(@NotNull Runnable runnedThread) {

                    }

                    @Override
                    public void onCompleted(@NotNull Runnable runnedThread, @NotNull Object returnedCallbacks) {
                        synchronized (stateThread) {
                            stateThread = true;
                            if (threadPerformCallbacks != null)
                                threadPerformCallbacks.onCompleted(CheckAdsUpdates.this, returnedCallbacks);
                        }
                    }

                    @Override
                    public void onRunning(@NotNull Runnable runnedThread, @NotNull Object returnedCallbacks) {

                    }

                    @Override
                    public void onCancelled(@NotNull Runnable runnedThread, @NotNull Throwable caused, @NotNull Object returnedCallbacks) {
                        synchronized (stateThread) {
                            stateThread = true;
                        }
                    }
                });
                Thread downloadFiles = new Thread(downloadIklanFiles, "DownloadFiles");
                downloadFiles.start();
                while (!stateThread) {
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                synchronizeIklanVersion(cloudVersion);
            }


        } else if (threadPerformCallbacks != null)
            threadPerformCallbacks.onCancelled(this, new ConnectException("Cannot Connect into the cloud!"), null);
    }

    private void synchronizeIklanVersion(int cloudVersion) {
        SharedPreferences shareds = service.getSharedPreferences(KeyListClasses.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String str = "" + cloudVersion;
        shareds.edit().putString(KeyListClasses.KEY_IKLAN_VERSION, str).commit();
    }

    private boolean cekKoneksi() {
        return CheckConnection.isConnected(service, 1000);
    }

    private int getCurrentAdsVersion() {
        SharedPreferences shareds = service.getSharedPreferences(KeyListClasses.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String str = shareds.getString(KeyListClasses.KEY_IKLAN_VERSION, null);
        if (str.equals("undefined")) return 0;
        else return Integer.parseInt(str);
    }
}
