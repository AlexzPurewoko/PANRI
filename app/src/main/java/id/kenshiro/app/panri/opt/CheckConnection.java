package id.kenshiro.app.panri.opt;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.mylexz.utils.MylexzActivity;

import org.jetbrains.annotations.NotNull;


public class CheckConnection {
    public static boolean isConnected(@NotNull MylexzActivity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        } else
            return false;
    }
}
