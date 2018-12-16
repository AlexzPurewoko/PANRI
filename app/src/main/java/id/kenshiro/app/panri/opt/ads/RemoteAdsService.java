package id.kenshiro.app.panri.opt.ads;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class RemoteAdsService implements Runnable {
    UpdateAdsService service;

    public RemoteAdsService(UpdateAdsService service) {
        this.service = service;
    }

    @Override
    public void run() {
        while (isApplicationRunning()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.gc();
        }
        service.stopSelf();
    }

    public boolean isApplicationRunning() {
        ActivityManager manager = (ActivityManager) service.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(10);
        for (ActivityManager.RunningTaskInfo componentInfo : runningTaskInfo) {
            ComponentName info = componentInfo.topActivity;
            Log.d("ServiceRemotes", "COmponent info Top Activity : " + info.getPackageName());
            if (info.getPackageName().equals(service.getPackageName()))
                return true;
        }
        return false;
    }
}
