package id.kenshiro.app.panri.opt.ads;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import id.kenshiro.app.panri.important.KeyListClasses;
import id.kenshiro.app.panri.opt.onsplash.ThreadPerformCallbacks;

public class GetCollectionDBThr implements Runnable {
    UpdateAdsService service;
    ThreadPerformCallbacks threadPerformCallbacks;
    List<DownloadIklanFiles.DBIklanCollection> collections;

    public GetCollectionDBThr(UpdateAdsService service, ThreadPerformCallbacks threadPerformCallbacks) {
        this.service = service;
        this.threadPerformCallbacks = threadPerformCallbacks;
    }

    @Override
    public void run() {
        if (getCurrentAdsVersion() == 0) return;
        if (threadPerformCallbacks != null)
            threadPerformCallbacks.onStarting(this);
        // lock the db
        while (service.isIklanFolderLocked.get()) {
            try {
                Thread.sleep(160);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        synchronized (service.isIklanFolderLocked) {
            service.isIklanFolderLocked.set(true);
        }
        collections = new ArrayList<>();
        extractMetadataFromSQL(new File(new File(service.getFilesDir(), KeyListClasses.FOLDER_IKLAN_CLOUD), KeyListClasses.NAME_IKLAN_DATABASES));
        // unlock the file
        synchronized (service.isIklanFolderLocked) {
            service.isIklanFolderLocked.set(false);
        }
        if (threadPerformCallbacks != null)
            threadPerformCallbacks.onCompleted(this, collections);
    }

    private int getCurrentAdsVersion() {
        SharedPreferences shareds = service.getSharedPreferences(KeyListClasses.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String str = shareds.getString(KeyListClasses.KEY_IKLAN_VERSION, null);
        if (str.equals("undefined")) return 0;
        else return Integer.parseInt(str);
    }

    private void extractMetadataFromSQL(File file) {
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(file, null);

        // name
        Cursor nameIklan = sqLiteDatabase.rawQuery("SELECT name FROM iklan", null);
        nameIklan.moveToFirst();
        while (nameIklan.isAfterLast()) {
            DownloadIklanFiles.DBIklanCollection dbIklanCollection = new DownloadIklanFiles.DBIklanCollection();
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
}
