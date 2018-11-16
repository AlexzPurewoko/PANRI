package id.kenshiro.app.panri.helper;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mylexz.utils.MylexzActivity;

import id.kenshiro.app.panri.MainActivity;
import id.kenshiro.app.panri.SplashScreenActivity;
import id.kenshiro.app.panri.important.KeyListClasses;

public class SwitchIntoMainActivity {
    public static void switchTo(@NonNull MylexzActivity activity, @NonNull Class<?> cls, @Nullable Bundle args) {
        activity.finish();
        System.gc();
        Intent a = new Intent(activity, cls);
        if (args != null)
            a.putExtras(args);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(a);
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        activity.finish();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Process.killProcess(Process.myPid());
            }
        }, 3000);

    }

    public static void switchToMain(@NonNull MylexzActivity activity) {
        Bundle args = new Bundle();
        args.putInt(KeyListClasses.DB_CONDITION_KEY, KeyListClasses.DB_IS_SAME_VERSION);
        args.putInt(KeyListClasses.APP_CONDITION_KEY, KeyListClasses.APP_IS_SAME_VERSION);
        switchTo(activity, MainActivity.class, args);
    }
}
