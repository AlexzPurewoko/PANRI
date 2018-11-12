package id.kenshiro.app.panri.opt.ads;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class UpdateAdsService extends Service {
    Thread remoteService;
    Thread checkAdsUpdate;
    Thread getAdsThread;

    @Override
    public void onCreate() {
        super.onCreate();
        remoteService = new Thread(new RemoteAdsService(this), "RemoteAdsService");
        remoteService.start();
        checkAdsUpdate = new Thread(new CheckAdsUpdates(this), "ServiceDownloadAndCheckIklan");
        checkAdsUpdate.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

}
