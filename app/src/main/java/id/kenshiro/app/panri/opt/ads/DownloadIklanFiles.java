package id.kenshiro.app.panri.opt.ads;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import id.kenshiro.app.panri.important.KeyListClasses;
import id.kenshiro.app.panri.opt.onsplash.ThreadPerformCallbacks;

public class DownloadIklanFiles implements Runnable {

    ThreadPerformCallbacks threadPerformCallbacks;
    FirebaseStorage firebaseStorage;
    int currVersion;
    Context c;
    Boolean state = false;
    File db;
    List<DBIklanCollection> collections;

    public DownloadIklanFiles(Context c, int currVersion, ThreadPerformCallbacks threadPerformCallbacks) {
        this.threadPerformCallbacks = threadPerformCallbacks;
        this.currVersion = currVersion;
        this.c = c;
    }

    @Override
    public void run() {
        if (currVersion == 0) return;
        if (threadPerformCallbacks != null)
            threadPerformCallbacks.onStarting(this);
        firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        StorageReference pathIklan = storageReference.child(KeyListClasses.FOLDER_IKLAN_CLOUD);
        StorageReference pathDB = storageReference.child(KeyListClasses.NAME_IKLAN_DATABASES);

        File disk = c.getFilesDir();
        disk.mkdirs();
        db = new File(disk, KeyListClasses.NAME_IKLAN_DATABASES);
        pathDB.getFile(db)
                .addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                        if (task.isComplete()) {
                            synchronized (state) {
                                state = true;
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        synchronized (state) {
                            state = true;
                        }
                    }
                });
        while (!state) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (!db.exists()) {
            if (threadPerformCallbacks != null)
                threadPerformCallbacks.onCancelled(this, new IOException("Cannot create the File cache for store iklan"), "" + currVersion);
            return;
        }
        // extract the metadata from SQLiteDatabase
        collections = new ArrayList<>();
        extractMetadataFromSQL(db);
        downloadIklanData(pathIklan);
        if (threadPerformCallbacks != null)
            threadPerformCallbacks.onCompleted(this, null);
    }

    private void downloadIklanData(StorageReference pathIklan) {
        state = false;
        Thread downloadIklanData = new Thread(new IklanUpdaterThread(pathIklan, c.getFilesDir(), collections, new ThreadPerformCallbacks() {
            @Override
            public void onStarting(@NotNull Runnable runnedThread) {

            }

            @Override
            public void onCompleted(@NotNull Runnable runnedThread, @NotNull Object returnedCallbacks) {
                synchronized (state) {
                    state = true;
                }
            }

            @Override
            public void onRunning(@NotNull Runnable runnedThread, @NotNull Object returnedCallbacks) {

            }

            @Override
            public void onCancelled(@NotNull Runnable runnedThread, @NotNull Throwable caused, @NotNull Object returnedCallbacks) {
                synchronized (state) {
                    state = true;
                }
            }
        }), "DownloadIklanThreads");
        downloadIklanData.start();
        while (!state) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void extractMetadataFromSQL(File file) {
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(file, null);
        Cursor nameIklan = sqLiteDatabase.rawQuery("SELECT name FROM iklan", null);
        nameIklan.moveToFirst();
        while (nameIklan.isAfterLast()) {
            DBIklanCollection dbIklanCollection = new DBIklanCollection();
            dbIklanCollection.nameImg = nameIklan.getString(0);
            collections.add(dbIklanCollection);
            nameIklan.moveToNext();
        }
        nameIklan.close();

        // gets the url from database
        int pos = 0;
        Cursor urlIklan = sqLiteDatabase.rawQuery("SELECT url FROM iklan", null);
        urlIklan.moveToFirst();
        while (urlIklan.isAfterLast()) {
            collections.get(pos).url = urlIklan.getString(0);
            urlIklan.moveToNext();
        }
        urlIklan.close();

        // gets the placed_on from database
        pos = 0;
        Cursor placedON = sqLiteDatabase.rawQuery("SELECT placed_on FROM iklan", null);
        placedON.moveToFirst();
        while (placedON.isAfterLast()) {
            collections.get(pos).putAt = placedON.getString(0);
            placedON.moveToNext();
        }
        placedON.close();

        // close the database
        sqLiteDatabase.close();
    }

    public class DBIklanCollection {
        String url;
        String nameImg;
        String putAt;

        public DBIklanCollection() {
        }
    }

}
