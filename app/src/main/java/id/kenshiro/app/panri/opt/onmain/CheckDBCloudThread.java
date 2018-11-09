package id.kenshiro.app.panri.opt.onmain;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mylexz.utils.MylexzActivity;

import java.util.HashMap;
import java.util.Map;

import id.kenshiro.app.panri.important.KeyListClasses;
import id.kenshiro.app.panri.opt.onsplash.ThreadPerformCallbacks;

public class CheckDBCloudThread implements Runnable {
    private ThreadPerformCallbacks threadPerformCallbacks = null;
    private MylexzActivity activity;
    FirebaseStorage firebaseStorage;
    String[] fetchedVerson = null;

    public CheckDBCloudThread(MylexzActivity activity, ThreadPerformCallbacks threadPerformCallbacks) {
        this.activity = activity;
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
        SharedPreferences shareds = activity.getSharedPreferences(KeyListClasses.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        //String obj = shareds.getString(KeyListClasses.)
        Map<String, Object> returned = new HashMap<>();
        returned.put(KeyListClasses.KEY_DATA_LIBRARY_VERSION, shareds.getString(KeyListClasses.KEY_DATA_LIBRARY_VERSION, null));
        returned.put(KeyListClasses.KEY_IKLAN_VERSION, shareds.getString(KeyListClasses.KEY_IKLAN_VERSION, null));
        return returned;
    }
}
