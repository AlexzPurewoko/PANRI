package id.kenshiro.app.panri.opt;

import com.crashlytics.android.Crashlytics;

public class LogIntoCrashlytics {
    public static void logException(String key, String message, Throwable e) {
        Crashlytics.setString(key, message);
        Crashlytics.logException(e);
    }
}
