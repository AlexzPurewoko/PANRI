package id.kenshiro.app.panri.opt.ads;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.storage.FirebaseStorage;
import com.mylexz.utils.MylexzActivity;

import java.lang.ref.WeakReference;
import java.util.List;

import id.kenshiro.app.panri.important.KeyListClasses;


public class SendAdsBReceiver extends BroadcastReceiver {
    private WeakReference<MylexzActivity> activityWeakReference;
    private OnReceiveAds receiveAdscallbacks;

    public SendAdsBReceiver(MylexzActivity activity, OnReceiveAds onReceiveAdscallbacks) {
        activityWeakReference = new WeakReference<>(activity);
        this.receiveAdscallbacks = onReceiveAdscallbacks;
    }

    public SendAdsBReceiver() {
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        // open the bundle extras
        Bundle received = intent.getExtras();
        if (receiveAdscallbacks != null)
            receiveAdscallbacks.onReceiveByteAds((GetResultedIklanThr.ByteArray[]) received.getParcelableArray(KeyListClasses.EXTRA_LIST_IKLAN_FILE_BYTES), (DownloadIklanFiles.DBIklanCollection[]) received.getParcelableArray(KeyListClasses.EXTRA_LIST_INFO_IKLAN));
    }

    public interface OnReceiveAds {
        void onReceiveByteAds(GetResultedIklanThr.ByteArray[] ads, DownloadIklanFiles.DBIklanCollection[] information);
    }
}
