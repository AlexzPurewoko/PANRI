package id.kenshiro.app.panri.opt;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.mylexz.utils.MylexzActivity;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;


public class CheckConnection {
    public static boolean isConnected(@NotNull Context activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return isReachableNetwoorks();
        } else
            return false;
    }

    private static boolean isReachableNetwoorks() {
        try {
            InetAddress inetAddress = InetAddress.getByName("google.com");
            if (inetAddress.toString().length() > 1)
                return true;
            else
                return false;
        } catch (UnknownHostException e) {
            return false;
        }
    }
}
