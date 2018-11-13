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
    private static Boolean isReachable = false;
    private static Boolean threadState = false;

    public static boolean isConnected(@NotNull Context activity, long expired) throws InterruptedException {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            /*IsNetworkReachable isReachableNet = new IsNetworkReachable();
            isReachableNet.run();
            int splice = Math.round(expired / 100);
            for(int x = 0; x < splice; x++) {
                Thread.sleep(100);
                if(threadState)
                    break;
            }
            if(isReachableNet.isAlive()) {
                isReachableNet.interrupt();
                return false;
            }
            else
                return isReachable;*/
            isReachable = isReachableNetwoorks();
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

    private static class IsNetworkReachable extends Thread {
        IsNetworkReachable() {
            super("CheckNetworks");
        }

        @Override
        public void run() {
            synchronized (threadState) {
                threadState = false;
            }
            try {
                InetAddress address = InetAddress.getByName("google.com");
                if (address.toString().length() > 1)
                    synchronized (isReachable) {
                        isReachable = true;
                    }
            } catch (UnknownHostException e) {
                synchronized (isReachable) {
                    isReachable = false;
                }
            }
            synchronized (threadState) {
                threadState = true;
            }
        }
    }
}
