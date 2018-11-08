package id.kenshiro.app.panri.opt;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;

import com.mylexz.utils.MylexzActivity;

public class CheckConnection {
    public static boolean isConnected(@NonNull MylexzActivity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
