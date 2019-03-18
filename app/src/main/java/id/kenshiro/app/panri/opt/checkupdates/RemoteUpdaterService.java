package id.kenshiro.app.panri.opt.checkupdates;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import java.util.List;

public class RemoteUpdaterService implements Runnable {
    CheckDBUpdaterService service;

    public RemoteUpdaterService(CheckDBUpdaterService service) {
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
        }
        service.stopSelf();
    }

    public boolean isApplicationRunning() {
        ActivityManager manager = (ActivityManager) service.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(10);
        for (ActivityManager.RunningTaskInfo componentInfo : runningTaskInfo) {
            ComponentName info = componentInfo.topActivity;
            if (info.getPackageName().equals(service.getPackageName()))
                return true;
        }
        return false;
    }
}
