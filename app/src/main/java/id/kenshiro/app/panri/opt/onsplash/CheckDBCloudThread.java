package id.kenshiro.app.panri.opt.onsplash;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.mylexz.utils.BuildConfig;
import com.mylexz.utils.MylexzActivity;
import com.mylexz.utils.SimpleDiskLruCache;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

import id.kenshiro.app.panri.SplashScreenActivity;
import id.kenshiro.app.panri.important.KeyListClasses;

public class CheckDBCloudThread implements Runnable {
    private ThreadPerformCallbacks threadPerformCallbacks = null;
    public static final String KEY_SHARED_DATA_DB_VERSION = "data_library_version";
    private MylexzActivity ctx;
    private String[] outStr = new String[2];

    public CheckDBCloudThread(@NotNull MylexzActivity ctx, ThreadPerformCallbacks threadPerformCallbacks) {
        this.ctx = ctx;
        this.threadPerformCallbacks = threadPerformCallbacks;
    }

    @Override
    public void run() {
        if (threadPerformCallbacks != null)
            threadPerformCallbacks.onStarting(this);
        //
        final String orig = getOriginalDBVersion();
        if (orig == null) {
            return;
        }
        outStr[0] = orig;
        // set the firebase
        final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings firebaseRemoteConfigSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        firebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(ctx, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String out = firebaseRemoteConfig.getString("data_library_version");

                        if (out != null && out.length() > 0) {
                            outStr[1] = out;
                            if (threadPerformCallbacks != null)
                                threadPerformCallbacks.onCompleted(CheckDBCloudThread.this, outStr);
                        } else {
                            outStr[1] = orig;
                            if (threadPerformCallbacks != null)
                                threadPerformCallbacks.onCompleted(CheckDBCloudThread.this, outStr);
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        outStr[1] = orig;
                        if (threadPerformCallbacks != null)
                            threadPerformCallbacks.onCancelled(CheckDBCloudThread.this, e, outStr);
                    }
                });

    }

    private String getOriginalDBVersion() {
        String getDBVersFromAssets = null;
        try {
            getDBVersFromAssets = getDBVersFromAssets();
        } catch (IOException e) {
            if (threadPerformCallbacks != null)
                threadPerformCallbacks.onCancelled(CheckDBCloudThread.this, e, null);
            return null;
        }
        synchronized (ctx) {
            SharedPreferences sharedPreferences = ctx.getSharedPreferences(KeyListClasses.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            if (!sharedPreferences.contains(KEY_SHARED_DATA_DB_VERSION)) {
                if (getDBVersFromAssets != null)
                    sharedPreferences.edit().putString(KEY_SHARED_DATA_DB_VERSION, getDBVersFromAssets).commit();
                return getDBVersFromAssets;
            } else {
                return sharedPreferences.getString(KEY_SHARED_DATA_DB_VERSION, getDBVersFromAssets);
            }
        }
    }

    private String getDBVersFromAssets() throws IOException {
        AssetManager assetManager = ctx.getAssets();
        InputStream is = assetManager.open("db_version");
        byte[] buff = new byte[is.available()];
        is.read(buff);
        final String out = new String(buff);
        is.close();
        return out;
    }

    public void setThreadPerformCallbacks(ThreadPerformCallbacks threadPerformCallbacks) {
        this.threadPerformCallbacks = threadPerformCallbacks;
    }
}
