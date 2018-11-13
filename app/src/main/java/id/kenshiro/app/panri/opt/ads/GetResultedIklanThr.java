package id.kenshiro.app.panri.opt.ads;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import id.kenshiro.app.panri.important.KeyListClasses;
import id.kenshiro.app.panri.opt.onsplash.ThreadPerformCallbacks;

public class GetResultedIklanThr implements Runnable {
    UpdateAdsService service;
    int requestedPosition;
    ThreadPerformCallbacks threadPerformCallbacks;
    List<DownloadIklanFiles.DBIklanCollection> collections;

    public GetResultedIklanThr(UpdateAdsService service, List<DownloadIklanFiles.DBIklanCollection> collections, int requestedPosition) {
        this.service = service;
        this.requestedPosition = requestedPosition;
        this.collections = collections;
    }

    @Override
    public void run() {
        // if ads is undefined, then stop it !
        if (getCurrentAdsVersion() == 0) {
            Bundle args = new Bundle();
            args.putParcelableArray(KeyListClasses.EXTRA_LIST_IKLAN_FILE_BYTES, null);
            args.putParcelableArray(KeyListClasses.EXTRA_LIST_INFO_IKLAN, null);
            Intent broadcast = new Intent(KeyListClasses.INTENT_BROADCAST_SEND_IKLAN);
            broadcast.putExtras(args);
            service.sendBroadcast(broadcast);
            return;
        }
        while (service.isIklanFolderLocked.get()) {
            try {
                Thread.sleep(90);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        synchronized (service.isIklanFolderLocked) {
            service.isIklanFolderLocked.set(true);
        }
        List<DownloadIklanFiles.DBIklanCollection> requested = new ArrayList<>();
        for (DownloadIklanFiles.DBIklanCollection data : collections) {
            if (data.placed_on == requestedPosition) {
                requested.add(data);
            }
        }

        DownloadIklanFiles.DBIklanCollection[] requestedArr = new DownloadIklanFiles.DBIklanCollection[requested.size()];
        // try to opening the files and gets the array of bytes
        Bundle args = new Bundle();
        ByteArray[] listGifByte = new ByteArray[requested.size()];
        File path_iklan = new File(service.getFilesDir(), KeyListClasses.FOLDER_IKLAN_CLOUD);
        int x = 0;
        for (DownloadIklanFiles.DBIklanCollection dataRequested : requested) {
            requestedArr[x] = dataRequested;
            File ads = new File(path_iklan, dataRequested.nameImg);
            try {
                InputStream is = new FileInputStream(ads);
                byte[] arr = new byte[is.available()];
                is.read(arr);
                is.close();

                listGifByte[x] = new ByteArray(arr);
            } catch (IOException e) {
                e.printStackTrace();
            }
            x++;
        }
        args.putParcelableArray(KeyListClasses.EXTRA_LIST_IKLAN_FILE_BYTES, listGifByte);
        args.putParcelableArray(KeyListClasses.EXTRA_LIST_INFO_IKLAN, requestedArr);

        // then, start the broadcast intent
        Intent broadcast = new Intent(KeyListClasses.INTENT_BROADCAST_SEND_IKLAN);
        broadcast.putExtras(args);
        service.sendBroadcast(broadcast);
        // unlock object
        synchronized (service.isIklanFolderLocked) {
            service.isIklanFolderLocked.set(false);
        }
    }

    public static class ByteArray implements Parcelable {
        byte[] array;
        public static final Parcelable.Creator<ByteArray> CREATOR = new Parcelable.Creator<ByteArray>() {

            @Override
            public ByteArray createFromParcel(Parcel source) {
                return new ByteArray(source);
            }

            @Override
            public ByteArray[] newArray(int size) {
                return new ByteArray[size];
            }
        };

        public ByteArray(Parcel in) {
            array = new byte[in.readInt()];
            in.readByteArray(array);
        }

        public ByteArray(byte[] array) {
            this.array = array;
        }

        public void setArray(byte[] array) {
            this.array = array;
        }

        public byte[] getArray() {
            return array;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.array.length);
            dest.writeByteArray(array);
        }
    }

    private int getCurrentAdsVersion() {
        SharedPreferences shareds = service.getSharedPreferences(KeyListClasses.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String str = shareds.getString(KeyListClasses.KEY_IKLAN_VERSION, null);
        if (str.equals("undefined")) return 0;
        else return Integer.parseInt(str);
    }
}
