package id.kenshiro.app.panri.opt.ads;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import id.kenshiro.app.panri.opt.onsplash.ThreadPerformCallbacks;

public class IklanUpdaterThread implements Runnable {
    private StorageReference pathIklan;
    private ThreadPerformCallbacks threadPerformCallbacks;
    private List<DownloadIklanFiles.DBIklanCollection> collections;
    private File disk;
    private AtomicBoolean state_success = new AtomicBoolean();

    public IklanUpdaterThread(StorageReference pathIklan, File disk, List<DownloadIklanFiles.DBIklanCollection> collections, ThreadPerformCallbacks threadPerformCallbacks) {
        this.pathIklan = pathIklan;
        this.threadPerformCallbacks = threadPerformCallbacks;
        this.collections = collections;
        this.disk = disk;
    }

    @Override
    public void run() {
        if (threadPerformCallbacks != null)
            threadPerformCallbacks.onStarting(this);
        StorageReference iklanFiles = null;
        for (int x = 0; x < collections.size(); x++) {
            state_success.set(false);
            DownloadIklanFiles.DBIklanCollection db = collections.get(x);
            iklanFiles = pathIklan.child(db.nameImg);
            if (threadPerformCallbacks != null)
                threadPerformCallbacks.onRunning(this, db.nameImg);
            File iklan = new File(disk, db.nameImg);
            iklanFiles.getFile(iklan)
                    .addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                            if (task.isComplete())
                                synchronized (state_success) {
                                    state_success.set(true);
                                }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            synchronized (state_success) {
                                state_success.set(true);
                            }
                        }
                    });

            while (!state_success.get()) {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        // completed!!!
        if (threadPerformCallbacks != null)
            threadPerformCallbacks.onCompleted(this, null);
    }
}
