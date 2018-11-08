package id.kenshiro.app.panri.opt.onsplash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
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

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import id.kenshiro.app.panri.BuildConfig;
import id.kenshiro.app.panri.MainActivity;
import id.kenshiro.app.panri.R;
import id.kenshiro.app.panri.SplashScreenActivity;
import id.kenshiro.app.panri.helper.CheckAndMoveDB;
import id.kenshiro.app.panri.opt.CheckConnection;
import io.fabric.sdk.android.Fabric;

public class LoaderTask extends AsyncTask<Void, Integer, Integer> {
    String folder_app_version = "app_version";
    String file_db_version = "db_version";
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
        try {
            // if is firstUsage then execute command below
            checkAndSaveAppVersion();
            //updateDBIfItsNewVersion();
        } catch (IOException e) {
            ctx.LOGE("Task.background()", "IOException occured when executing checkAndSaveAppVersion() & updateDBIfItsNewVersion();", e);
        }
        // creates cache directory if not exists
        try {
            diskCache = SimpleDiskLruCache.getsInstance(fileCache);
        } catch (IOException e) {
            ctx.LOGE("Task.background()", "IOException occured when initialize DiskLruObjectCache instance", e);
        }
        publishProgress(0);
        synchronized (this) {
            switch (ctx.app_condition) {
                case SplashScreenActivity.APP_IS_OLDER_VERSION:
                case SplashScreenActivity.APP_IS_NEWER_VERSION: {
                    cleanCache();
                    createCacheOperation();
                }
                break;
                case SplashScreenActivity.APP_IS_FIRST_USAGE: {
                    ctx.getFilesDir().mkdir();
                    try {
                        ExtractAndConfigureData.extractData(ctx, ctx.getFilesDir(), "data_panri.zip");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    createCacheOperation();
                }
                break;
                case SplashScreenActivity.APP_IS_SAME_VERSION: {
                    boolean isConnected = CheckConnection.isConnected(ctx);
                    if (!isConnected) {
                        boolean status_cache_dirs = validateCacheDirs();
                        if (status_cache_dirs) {
                            createCacheOperation();
                        }
                    } else {
                        FirebaseApp.initializeApp(ctx);
                        // start separate threads to perform an action
                        numOfThreads = 2;
                        CheckDBCloudThread checkDBCloudThread = new CheckDBCloudThread(ctx, new ThreadPerformCallbacks() {
                            @Override
                            public void onStarting(@NotNull Runnable runnedThread) {

                            }

                            @Override
                            public void onCompleted(@NotNull Runnable runnedThread, @NotNull Object returnedCallbacks) {
                                synchronized (finishedThreads) {
                                    finishedThreads++;
                                    dbversion = (String[]) returnedCallbacks;
                                }
                            }

                            @Override
                            public void onCancelled(@NotNull Runnable runnedThread, @NotNull Throwable caused, @NotNull Object returnedCallbacks) {
                                Crashlytics.logException(caused);
                                finishedThreads++;
                                dbversion = (String[]) returnedCallbacks;
                            }
                        });
                        CheckCacheAndConfThread checkCacheAndConfThread = new CheckCacheAndConfThread(ctx, diskCache);
                        checkCacheAndConfThread.setThreadPerformCallbacks(new ThreadPerformCallbacks() {
                            @Override
                            public void onStarting(@NotNull Runnable runnedThread) {

                            }

                            @Override
                            public void onCompleted(@NotNull Runnable runnedThread, @NotNull Object returnedCallbacks) {
                                synchronized (finishedThreads) {
                                    finishedThreads++;
                                }
                            }

                            @Override
                            public void onCancelled(@NotNull Runnable runnedThread, @NotNull Throwable caused, @NotNull Object returnedCallbacks) {

                            }
                        });

                        // starts it!
                        Thread t1 = new Thread(checkCacheAndConfThread), t2 = new Thread(checkDBCloudThread);
                        t1.start();
                        t2.start();
                    }
                }
            }
        }
        publishProgress(1);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            ctx.LOGE("Task.background()", "Interrupted signal exception!", e);
        }
        try {
            diskCache.close();
        } catch (IOException e) {
            ctx.LOGE("Task.background()", "IOException occured when closing diskCache", e);
        }
        SharedPreferences shareds = ctx.getSharedPreferences(SplashScreenActivity.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        if (!shareds.contains(ctx.KEY_SHARED_DATA_CURRENT_IMG_NAVHEADER))
            shareds.edit().putInt(ctx.KEY_SHARED_DATA_CURRENT_IMG_NAVHEADER, 0).commit();
        int curr = shareds.getInt(ctx.KEY_SHARED_DATA_CURRENT_IMG_NAVHEADER, 0);
        if (curr == 4)
            curr = 0;
        else
            curr++;
        shareds.edit().putInt(ctx.KEY_SHARED_DATA_CURRENT_IMG_NAVHEADER, curr).commit();
        // wait for the threads
        while (finishedThreads != numOfThreads) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return ctx.app_condition;
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

    /*
    private void updateDBIfItsNewVersion() throws IOException {
        File path = new File(ctx.getApplicationInfo().dataDir, "files");
        path.mkdir();
        File content = new File(path, this.file_db_version);
        if (!content.exists()) {
            AssetManager assetManager = ctx.getAssets();
            InputStream fis = assetManager.open(this.file_db_version);
            FileOutputStream fos = new FileOutputStream(content);
            int read;
            while ((read = fis.read()) != -1) {
                fos.write(read);
            }
            fis.close();
            fos.close();
            ctx.db_condition = SplashScreenActivity.DB_IS_FIRST_USAGE;
            // update into databases
            new CheckAndMoveDB(ctx, "database_penyakitpadi.db").upgradeDB();
        }
        // if exists
        else {
            int current_db_version = getDBVersion();
            int available_db_version = getDBVersionInAssets();
            // apply newer version
            if (current_db_version < available_db_version) {
                AssetManager assetManager = ctx.getAssets();
                InputStream fis = assetManager.open(this.file_db_version);
                FileOutputStream fos = new FileOutputStream(content);
                int read;
                while ((read = fis.read()) != -1) {
                    fos.write(read);
                }
                fis.close();
                fos.close();
                // update into databases
                new CheckAndMoveDB(ctx, "database_penyakitpadi.db").upgradeDB();
                ctx.db_condition = SplashScreenActivity.DB_IS_NEWER_VERSION;
            } else if (current_db_version > available_db_version)
                ctx.db_condition = SplashScreenActivity.DB_IS_OLDER_IN_APP_VERSION;
            else
                ctx.db_condition = SplashScreenActivity.DB_IS_SAME_VERSION;
        }

    }

    private int getDBVersionInAssets() throws IOException {
        // gets the version
        InputStream fis = ctx.getAssets().open(this.file_db_version);
        byte[] available = new byte[fis.available()];
        fis.read(available);
        String out = new String(available);
        fis.close();
        return Integer.parseInt(out);
    }

    private int getDBVersion() throws IOException {
        File path = new File(ctx.getApplicationInfo().dataDir, "files");
        File content = new File(path, this.file_db_version);
        // gets the version
        FileInputStream fis = new FileInputStream(content);
        byte[] available = new byte[fis.available()];
        fis.read(available);
        String out = new String(available);
        fis.close();
        return Integer.parseInt(out);
    }
*/
    private void checkAndSaveAppVersion() throws IOException {
        int version = BuildConfig.VERSION_CODE;
        File path = new File(ctx.getApplicationInfo().dataDir, "files");
        path.mkdir();
        File content = new File(path, this.folder_app_version);
        if (!content.exists()) {
            ctx.app_condition = SplashScreenActivity.APP_IS_FIRST_USAGE;
            String data = String.valueOf(version);
            FileOutputStream fos = new FileOutputStream(content);
            fos.write(data.getBytes());
            fos.close();
        } else {
            // check app version and if its newer, push command to Bundle args
            FileInputStream fis = new FileInputStream(content);
            byte[] buf = new byte[fis.available()];
            fis.read(buf);
            int current_version = Integer.parseInt(new String(buf));
            fis.close();
            if (current_version < version) {
                ctx.app_condition = SplashScreenActivity.APP_IS_NEWER_VERSION;
                String data = String.valueOf(version);
                FileOutputStream fos = new FileOutputStream(content);
                fos.write(data.getBytes());
                fos.close();
            } else if (current_version > version) {
                ctx.app_condition = SplashScreenActivity.APP_IS_OLDER_VERSION;
            } else
                ctx.app_condition = SplashScreenActivity.APP_IS_SAME_VERSION;
        }
    }

    @Override
    protected void onPostExecute(Integer aVoid) {
        super.onPostExecute(aVoid);
        if (dbversion != null) {
            int origVer = Integer.parseInt(dbversion[0]);
            int newVer = Integer.parseInt(dbversion[1]);
            if (newVer > origVer) {
                ctx.db_condition = SplashScreenActivity.DB_REQUEST_UPDATE;
            } else if (newVer == origVer) {
                ctx.db_condition = SplashScreenActivity.DB_IS_SAME_VERSION;
            }
        }
        //gifImageView.setVisibility(View.GONE);
        //btnNext.setVisibility(View.VISIBLE);
        animateAndForward();

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (values[0] == 0) {
            ctx.indicators.setText("Membuat cache...");
        } else if (values[0] == 1) {
            ctx.indicators.setText("Membuka aplikasi");
        }
    }

    private void animateAndForward() {
        Fade in = new Fade(Fade.IN);
        in.setDuration(1200);
        TransitionManager.beginDelayedTransition((RelativeLayout) ctx.findViewById(R.id.actsplash_id_bawah_layout), in);
        ctx.linearIndicator.setVisibility(View.GONE);
        if (ctx.app_condition == SplashScreenActivity.APP_IS_FIRST_USAGE)
            ctx.btnNext.setVisibility(View.VISIBLE);
        else {
            Bundle args = new Bundle();
            args.putInt(SplashScreenActivity.APP_CONDITION_KEY, ctx.app_condition);
            args.putInt(SplashScreenActivity.DB_CONDITION_KEY, ctx.db_condition);
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