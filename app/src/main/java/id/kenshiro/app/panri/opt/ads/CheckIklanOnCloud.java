package id.kenshiro.app.panri.opt.ads;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import id.kenshiro.app.panri.important.KeyListClasses;
import id.kenshiro.app.panri.opt.onsplash.ThreadPerformCallbacks;

public class CheckIklanOnCloud implements Runnable {
    ThreadPerformCallbacks threadPerformCallbacks;
    FirebaseStorage firebaseStorage;
    int currVersion;

    public CheckIklanOnCloud(int currVersion, ThreadPerformCallbacks threadPerformCallbacks) {
        this.threadPerformCallbacks = threadPerformCallbacks;
        this.currVersion = currVersion;
    }

    @Override
    public void run() {
        if (threadPerformCallbacks != null)
            threadPerformCallbacks.onStarting(this);
        firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        StorageReference pathFile = storageReference.child(KeyListClasses.KEY_IKLAN_VERSION);
        pathFile.getBytes(1024)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {

                    @Override
                    public void onSuccess(byte[] bytes) {
                        String out = new String(bytes);
                        if (threadPerformCallbacks != null)
                            threadPerformCallbacks.onCompleted(CheckIklanOnCloud.this, out);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (threadPerformCallbacks != null)
                            threadPerformCallbacks.onCompleted(CheckIklanOnCloud.this, "" + currVersion);
                    }
                });
    }

}
