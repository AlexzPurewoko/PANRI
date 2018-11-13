package id.kenshiro.app.panri.opt.onsplash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.transition.Fade;
import android.support.transition.TransitionManager;
import android.view.View;
import android.widget.RelativeLayout;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.FirebaseApp;
import com.mylexz.utils.MylexzActivity;
import com.mylexz.utils.SimpleDiskLruCache;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import id.kenshiro.app.panri.BuildConfig;
import id.kenshiro.app.panri.MainActivity;
import id.kenshiro.app.panri.R;
import id.kenshiro.app.panri.SplashScreenActivity;
import id.kenshiro.app.panri.important.KeyListClasses;
import id.kenshiro.app.panri.opt.CheckConnection;
import id.kenshiro.app.panri.opt.LogIntoCrashlytics;
import id.kenshiro.app.panri.opt.ads.UpdateAdsService;

public class LoaderTask extends AsyncTask<Void, String, Integer> {
    File fileCache;
    SimpleDiskLruCache diskCache;
    SplashScreenActivity ctx;
    int numOfThreads = 0;
    volatile Integer finishedThreads = 0;
    volatile String[] dbversion = null;

    public LoaderTask(SplashScreenActivity ctx) {
        this.ctx = ctx;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        fileCache = new File(ctx.getCacheDir(), "cache");
        fileCache.mkdir();
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        publishProgress("First checking...");
        try {
            // if is firstUsage then execute command below
            checkAndSaveAppVersion();
            Crashlytics.setString("IOExceptionCheckAppVers", "Success");
            //updateDBIfItsNewVersion();
        } catch (IOException e) {
            LogIntoCrashlytics.logException("IOExceptionCheckAppVers", String.format("IOException occured when executing checkAndSaveAppVersion() e -> %s", e.toString()), e);
            ctx.LOGE("Task.background()", "IOException occured when executing checkAndSaveAppVersion() & updateDBIfItsNewVersion();", e);
        }
        // creates cache directory if not exists
        try {
            diskCache = SimpleDiskLruCache.getsInstance(fileCache);
        } catch (IOException e) {
            LogIntoCrashlytics.logException("IOExceptionDiskCache", String.format("IOException occured when initialize DiskLruObjectCache instance e -> %s", e.toString()), e);
            ctx.LOGE("Task.background()", "IOException occured when initialize DiskLruObjectCache instance", e);
        }
        publishProgress("Mempersiapkan data...");
        synchronized (this) {
            switch (ctx.app_condition) {
                case KeyListClasses.APP_IS_OLDER_VERSION:
                    break;
                case KeyListClasses.APP_IS_NEWER_VERSION: {
                    cleanCache();
                    updateAppDataInApp();
                    createCacheOperation();
                    try {
                        String db_inapk_version = ExtractAndConfigureData.getStringFromAssets(ctx, "db_version");
                        String db_current_version = ExtractAndConfigureData.getStringFromShareds(ctx, KeyListClasses.KEY_DATA_LIBRARY_VERSION, null);
                        int dbcurr = Integer.parseInt(db_current_version);
                        int dbleast = Integer.parseInt(db_inapk_version);
                        if (dbleast > dbcurr) {
                            ExtractAndConfigureData.configureStringInShareds(ctx, KeyListClasses.KEY_DATA_LIBRARY_VERSION, db_inapk_version);
                            ctx.db_condition = KeyListClasses.DB_IS_NEWER_VERSION;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
                case KeyListClasses.APP_IS_FIRST_USAGE: {
                    ctx.getFilesDir().mkdir();
                    try {
                        ExtractAndConfigureData.extractData(ctx, ctx.getFilesDir(), "data_panri.zip");
                        File iklan = new File(ctx.getFilesDir(), KeyListClasses.FOLDER_IKLAN_CLOUD);
                        iklan.mkdirs();
                        if (getCurrentAdsVersionRenew() != 0)
                            ExtractAndConfigureData.extractData(ctx, iklan, KeyListClasses.NAME_IKLAN_ON_ASSETS_PACK);
                    } catch (IOException e) {
                        LogIntoCrashlytics.logException("IOExceptionExtractData", String.format("IOException occured when Extract and configure data e -> %s", e.toString()), e);
                        e.printStackTrace();
                    }
                    ExtractAndConfigureData.configureData(ctx);
                    createCacheOperation();
                }
                break;
                case KeyListClasses.APP_IS_SAME_VERSION: {
                    boolean isConnected = false;
                    try {
                        isConnected = CheckConnection.isConnected(ctx, 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        isConnected = false;
                    }
                    if (!isConnected) {
                        boolean status_cache_dirs = validateCacheDirs();
                        if (status_cache_dirs) {
                            createCacheOperation();
                        }
                    } else {
                        FirebaseApp.initializeApp(ctx);
                        // start separate threads to perform an action
                        boolean ischeckDB = isAllowedToCheckDBOnline();
                        numOfThreads = 1;
                        Crashlytics.log("Check DB and Check Cache in separate thread");
                        CheckDBCloudThread checkDBCloudThread = null;
                        CheckCacheAndConfThread checkCacheAndConfThread = new CheckCacheAndConfThread(ctx, diskCache);
                        checkCacheAndConfThread.setThreadPerformCallbacks(new ThreadPerformCallbacks() {
                            @Override
                            public void onStarting(@NotNull Runnable runnedThread) {
                                Crashlytics.log("Check Cache in separate thread started!");
                            }

                            @Override
                            public void onCompleted(@NotNull Runnable runnedThread, @NotNull Object returnedCallbacks) {
                                synchronized (finishedThreads) {
                                    Crashlytics.log("Check Cache in separate thread completed!");
                                    finishedThreads++;
                                }
                            }

                            @Override
                            public void onRunning(@NotNull Runnable runnedThread, @NotNull Object returnedCallbacks) {

                            }

                            @Override
                            public void onCancelled(@NotNull Runnable runnedThread, @NotNull Throwable caused, @NotNull Object returnedCallbacks) {

                            }
                        });
                        if (ischeckDB) {
                            numOfThreads = 2;
                            checkDBCloudThread = new CheckDBCloudThread(ctx, new ThreadPerformCallbacks() {
                                @Override
                                public void onStarting(@NotNull Runnable runnedThread) {
                                    Crashlytics.log("Check DB in separate thread started!");
                                }

                                @Override
                                public void onCompleted(@NotNull Runnable runnedThread, @NotNull Object returnedCallbacks) {
                                    synchronized (finishedThreads) {
                                        Crashlytics.log("Check DB in separate thread completed!");
                                        finishedThreads++;
                                        dbversion = (String[]) returnedCallbacks;
                                    }
                                }

                                @Override
                                public void onRunning(@NotNull Runnable runnedThread, @NotNull Object returnedCallbacks) {

                                }

                                @Override
                                public void onCancelled(@NotNull Runnable runnedThread, @NotNull Throwable caused, @NotNull Object returnedCallbacks) {
                                    //Crashlytics.logException(caused);
                                    Crashlytics.log(String.format("Check Cache in separate thread cancelled! because -> e: %s", caused.toString()));
                                    finishedThreads++;
                                    dbversion = (String[]) returnedCallbacks;
                                }
                            });
                        }
                        // starts it!
                        Thread t1 = new Thread(checkCacheAndConfThread), t2 = (checkDBCloudThread == null) ? null : new Thread(checkDBCloudThread);
                        t1.start();
                        if (t2 != null)
                            t2.start();
                    }
                }
            }
        }
        publishProgress("Hampir siap...");
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            LogIntoCrashlytics.logException("InterruptExSleep", String.format("InterruptedException! occured when sleep a main thread e -> %s", e.toString()), e);
            ctx.LOGE("Task.background()", "Interrupted signal exception!", e);
        }
        try {
            diskCache.close();
        } catch (IOException e) {
            LogIntoCrashlytics.logException("IOExCloseCache", String.format("IOException occured when closing diskCache e -> %s", e.toString()), e);
            ctx.LOGE("Task.background()", "IOException occured when closing diskCache", e);
        }
        SharedPreferences shareds = ctx.getSharedPreferences(KeyListClasses.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        int curr = shareds.getInt(KeyListClasses.KEY_SHARED_DATA_CURRENT_IMG_NAVHEADER, 0);
        if (curr == 4)
            curr = 0;
        else
            curr++;
        shareds.edit().putInt(KeyListClasses.KEY_SHARED_DATA_CURRENT_IMG_NAVHEADER, curr).commit();
        // wait for the threads
        while (finishedThreads < numOfThreads) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                LogIntoCrashlytics.logException("InterruptExSleep2", String.format("InterruptedException! occured when waiting the threads, finishedThreads(%d) e -> %s", finishedThreads, e.toString()), e);
            }
        }
        publishProgress("Selesai!");
        Crashlytics.log("Thread ended!");
        return ctx.app_condition;
    }

    private int getCurrentAdsVersionRenew() throws IOException {
        final String out = ExtractAndConfigureData.getStringFromAssets(ctx, "iklan_version");
        if (out.equals("undefined")) return 0;
        else return Integer.parseInt(out);
    }

    private void updateAppDataInApp() {
        File disk = ctx.getFilesDir();
        // clean the files before update
        try {
            //FileUtils.deleteDirectory(disk);
            // directory data for app
            FileUtils.deleteDirectory(new File(disk, "data"));
            FileUtils.deleteDirectory(new File(disk, "data_hama_html"));
            FileUtils.deleteQuietly(new File(disk, "database_penyakitpadi.db"));
            File iklan = new File(disk, KeyListClasses.FOLDER_IKLAN_CLOUD);
            if (iklan.exists())
                FileUtils.deleteQuietly(iklan);
        } catch (IOException e) {
            String keyEx = "fileUtils_updateAppDataInApp_TaskDownloadDBUpdates";
            String resE = String.format("Cannot delete the selected directory e -> %s", e.toString());
            LogIntoCrashlytics.logException(keyEx, resE, e);
            ctx.LOGE(keyEx, resE);
        }
        disk.mkdirs();

        // extract the data
        try {
            ExtractAndConfigureData.extractData(ctx, disk, "data_panri.zip");
            File iklan = new File(ctx.getFilesDir(), KeyListClasses.FOLDER_IKLAN_CLOUD);
            iklan.mkdirs();
            if (getCurrentAdsVersionRenew() != 0)
                ExtractAndConfigureData.extractData(ctx, iklan, KeyListClasses.NAME_IKLAN_ON_ASSETS_PACK);
        } catch (IOException e) {
            LogIntoCrashlytics.logException("IOExceptionExtractData", String.format("IOException occured when Extract and configure data e -> %s", e.toString()), e);
            e.printStackTrace();
        }
    }

    private boolean isAllowedToCheckDBOnline() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(KeyListClasses.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KeyListClasses.KEY_AUTOCHECKUPDATE_APPDATA, true);
    }

    private synchronized void createCacheOperation() {
        CheckCacheAndConfThread checkCacheAndConfThread = new CheckCacheAndConfThread(ctx, diskCache);
        checkCacheAndConfThread.run();
    }

    private boolean validateCacheDirs() {
        String[] fileList = fileCache.list();
        if (fileList.length == 0)
            return true;
        return false;
    }

    private void cleanCache() {
        fileCache.delete();
        fileCache.mkdir();
    }

    private void checkAndSaveAppVersion() throws IOException {
        int version = BuildConfig.VERSION_CODE;
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(KeyListClasses.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        if (!sharedPreferences.contains(KeyListClasses.KEY_APP_VERSION))
            ctx.app_condition = KeyListClasses.APP_IS_FIRST_USAGE;
        else {
            int curr_app_version = sharedPreferences.getInt(KeyListClasses.KEY_APP_VERSION, 0);
            if (curr_app_version < version) {
                ctx.app_condition = KeyListClasses.APP_IS_NEWER_VERSION;
                sharedPreferences.edit().putInt(KeyListClasses.KEY_APP_VERSION, version).commit();
            } else if (curr_app_version > version) {
                ctx.app_condition = KeyListClasses.APP_IS_OLDER_VERSION;
            } else
                ctx.app_condition = KeyListClasses.APP_IS_SAME_VERSION;
        }

    }

    @Override
    protected void onPostExecute(Integer aVoid) {
        super.onPostExecute(aVoid);
        if (dbversion != null) {
            int origVer = Integer.parseInt(dbversion[0]);
            int newVer = Integer.parseInt(dbversion[1]);
            if (newVer > origVer) {
                ctx.db_condition = KeyListClasses.DB_REQUEST_UPDATE;
            } else if (newVer == origVer) {
                ctx.db_condition = KeyListClasses.DB_IS_SAME_VERSION;
            }
        }
        Intent intentService = new Intent(ctx, UpdateAdsService.class);
        intentService.putExtra(KeyListClasses.GET_ADS_MODE_START_SERVICE, KeyListClasses.IKLAN_MODE_START_SERVICE);
        ctx.startService(intentService);
        animateAndForward();

    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        ctx.indicators.setText(values[0]);
    }

    private void animateAndForward() {
        Fade in = new Fade(Fade.IN);
        in.setDuration(1200);
        TransitionManager.beginDelayedTransition((RelativeLayout) ctx.findViewById(R.id.actsplash_id_bawah_layout), in);
        ctx.linearIndicator.setVisibility(View.GONE);
        if (ctx.app_condition == KeyListClasses.APP_IS_FIRST_USAGE)
            ctx.btnNext.setVisibility(View.VISIBLE);
        else {
            Bundle args = new Bundle();
            args.putInt(KeyListClasses.APP_CONDITION_KEY, ctx.app_condition);
            args.putInt(KeyListClasses.DB_CONDITION_KEY, ctx.db_condition);
            args.putStringArray(KeyListClasses.KEY_LIST_VERSION_DB, dbversion);
            MylexzActivity activity = ctx;
            activity.finish();
            System.gc();
            Intent a = new Intent(activity, MainActivity.class);
            a.putExtras(args);
            activity.startActivity(a);
            activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }
}