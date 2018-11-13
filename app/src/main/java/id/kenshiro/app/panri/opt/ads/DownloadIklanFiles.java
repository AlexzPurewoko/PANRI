package id.kenshiro.app.panri.opt.ads;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import id.kenshiro.app.panri.important.KeyListClasses;
import id.kenshiro.app.panri.opt.onsplash.ThreadPerformCallbacks;

public class DownloadIklanFiles implements Runnable {

    ThreadPerformCallbacks threadPerformCallbacks;
    FirebaseStorage firebaseStorage;
    int currVersion;
    UpdateAdsService c;
    Boolean state = false;
    File db;
    List<DBIklanCollection> collections;

    public DownloadIklanFiles(UpdateAdsService c, int currVersion, ThreadPerformCallbacks threadPerformCallbacks) {
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
        db = new File(new File(disk, KeyListClasses.NAME_IKLAN_CACHE_PATH), KeyListClasses.NAME_IKLAN_DATABASES);
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
        // first locking the object before update iklan
        while (c.isIklanFolderLocked.get()) {
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        synchronized (c.isIklanFolderLocked) {
            c.isIklanFolderLocked.set(true);
        }
        try {
            moveIklanParams(new File(disk, KeyListClasses.FOLDER_IKLAN_CLOUD), new File(disk, KeyListClasses.NAME_IKLAN_CACHE_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (threadPerformCallbacks != null)
            threadPerformCallbacks.onCompleted(this, collections);
    }

    private synchronized void moveIklanParams(File iklan_path, File iklan_cache_dirs) throws IOException {
        FileUtils.deleteDirectory(iklan_path);
        iklan_path.delete();
        iklan_cache_dirs.renameTo(iklan_path);
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

        // name
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
            collections.get(pos).placed_on = placedON.getInt(0);
            placedON.moveToNext();
        }
        placedON.close();

        // gets the tipe_url from database
        pos = 0;
        Cursor tipe_url = sqLiteDatabase.rawQuery("SELECT tipe_url FROM iklan", null);
        tipe_url.moveToFirst();
        while (tipe_url.isAfterLast()) {
            collections.get(pos).tipe_url = tipe_url.getInt(0);
            tipe_url.moveToNext();
        }
        tipe_url.close();

        // gets the info_produk from database
        pos = 0;
        Cursor info_produk = sqLiteDatabase.rawQuery("SELECT info_produk FROM iklan", null);
        info_produk.moveToFirst();
        while (info_produk.isAfterLast()) {
            collections.get(pos).info_produk = info_produk.getString(0);
            info_produk.moveToNext();
        }
        info_produk.close();
        // close the database
        sqLiteDatabase.close();
    }

    public static class DBIklanCollection implements Serializable, Parcelable {
        String url;
        int tipe_url;
        String info_produk;
        String nameImg;
        int placed_on;
        public static final Parcelable.Creator<DBIklanCollection> CREATOR = new Parcelable.Creator<DBIklanCollection>() {

            @Override
            public DBIklanCollection createFromParcel(Parcel source) {
                return new DBIklanCollection(source);
            }

            @Override
            public DBIklanCollection[] newArray(int size) {
                return new DBIklanCollection[size];
            }
        };

        public DBIklanCollection(Parcel source) {

            String[] resultedStr = new String[3];
            int[] intresult = new int[2];
            source.readStringArray(resultedStr);
            source.readIntArray(intresult);

            // set str
            setUrl(resultedStr[0]);
            setInfo_produk(resultedStr[1]);
            setNameImg(resultedStr[2]);

            // set int
            setTipe_url(intresult[0]);
            setPlaced_on(intresult[1]);
        }
        public DBIklanCollection() {
        }

        public void setInfo_produk(String info_produk) {
            this.info_produk = info_produk;
        }

        public void setNameImg(String nameImg) {
            this.nameImg = nameImg;
        }

        public void setPlaced_on(int placed_on) {
            this.placed_on = placed_on;
        }

        public void setTipe_url(int tipe_url) {
            this.tipe_url = tipe_url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getPlaced_on() {
            return placed_on;
        }

        public int getTipe_url() {
            return tipe_url;
        }

        public String getInfo_produk() {
            return info_produk;
        }

        public String getNameImg() {
            return nameImg;
        }

        public String getUrl() {
            return url;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            String[] resultedStr = {
                    this.url,
                    this.info_produk,
                    this.nameImg
            };
            int[] resultedInt = {
                    this.tipe_url,
                    this.placed_on
            };
            dest.writeStringArray(resultedStr);
            dest.writeIntArray(resultedInt);
        }
    }

}
