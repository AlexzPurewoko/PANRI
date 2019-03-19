package id.kenshiro.app.panri.opt.onsplash;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mylexz.utils.MylexzActivity;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import id.kenshiro.app.panri.important.KeyListClasses;

public class CheckDBCloudThread implements Runnable {
    private ThreadPerformCallbacks threadPerformCallbacks = null;
    public static final String KEY_SHARED_DATA_DB_VERSION = "data_library_version";
    private FirebaseStorage firebaseStorage;
    private MylexzActivity ctx;
    private String[] outStr = new String[2];
    String[] fetchedVerson = null;

    public CheckDBCloudThread(@NotNull MylexzActivity ctx, ThreadPerformCallbacks threadPerformCallbacks) {
        this.ctx = ctx;
        this.threadPerformCallbacks = threadPerformCallbacks;
    }

    @Override
    public void run() {
        if (threadPerformCallbacks != null)
            threadPerformCallbacks.onStarting(this);
        final Map<String, Object> mapDefaults = getDefaults();
        firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        StorageReference pathReference = storageReference.child("db_version");
        fetchedVerson = new String[2];
        fetchedVerson[0] = (String) mapDefaults.get(KeyListClasses.KEY_DATA_LIBRARY_VERSION);
        pathReference.getBytes(1024)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        fetchedVerson[1] = new String(bytes);
                        if (threadPerformCallbacks != null)
                            threadPerformCallbacks.onCompleted(CheckDBCloudThread.this, fetchedVerson);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        synchronized (fetchedVerson) {
                            fetchedVerson[1] = fetchedVerson[0];
                            if (threadPerformCallbacks != null)
                                threadPerformCallbacks.onCompleted(CheckDBCloudThread.this, fetchedVerson);
                        }
                        Crashlytics.log("Failure while get db version on cloud, e -> " + e.toString());
                    }
                });
    }

    private Map<String, Object> getDefaults() {
        SharedPreferences shareds = ctx.getSharedPreferences(KeyListClasses.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        //String obj = shareds.getString(KeyListClasses.)
        Map<String, Object> returned = new HashMap<>();
        returned.put(KeyListClasses.KEY_DATA_LIBRARY_VERSION, shareds.getString(KeyListClasses.KEY_DATA_LIBRARY_VERSION, null));
        returned.put(KeyListClasses.KEY_IKLAN_VERSION, shareds.getString(KeyListClasses.KEY_IKLAN_VERSION, null));
        return returned;
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
