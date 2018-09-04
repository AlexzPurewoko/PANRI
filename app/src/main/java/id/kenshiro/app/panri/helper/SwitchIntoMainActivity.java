package id.kenshiro.app.panri.helper;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mylexz.utils.MylexzActivity;

import id.kenshiro.app.panri.MainActivity;

public class SwitchIntoMainActivity {
    public static void switchTo(@NonNull MylexzActivity activity, @NonNull Class<?> cls, @Nullable Bundle args) {
        activity.finish();
        System.gc();
        Intent a = new Intent(activity, cls);
        if (args != null)
            a.putExtras(args);
        activity.startActivity(a);
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public static void switchToMain(@NonNull MylexzActivity activity) {
        switchTo(activity, MainActivity.class, null);
    }
}
