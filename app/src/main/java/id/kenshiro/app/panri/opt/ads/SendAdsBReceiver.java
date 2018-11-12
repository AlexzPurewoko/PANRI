package id.kenshiro.app.panri.opt.ads;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.storage.FirebaseStorage;
import com.mylexz.utils.MylexzActivity;

import java.lang.ref.WeakReference;
import java.util.List;


public class SendAdsBReceiver extends BroadcastReceiver {
    private WeakReference<MylexzActivity> activityWeakReference;
    private OnReceiveAds receiveAdscallbacks;

    public SendAdsBReceiver(MylexzActivity activity, OnReceiveAds onReceiveAdscallbacks) {
        activityWeakReference = new WeakReference<>(activity);
        this.receiveAdscallbacks = onReceiveAdscallbacks;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

    }

    public interface OnReceiveAds {
        public void onReceive(List<String> pathToAds, List<String> urlImages);

        public void onReceiveByteAds(List<byte[]> ads, List<String> urlImages);
    }
}
