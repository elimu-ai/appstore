package ai.elimu.appstore.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import java.io.IOException;
import java.net.InetAddress;

import ai.elimu.appstore.BuildConfig;
import timber.log.Timber;

public class ConnectivityHelper {

    public static boolean isWifiEnabled(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo.isConnected();
    }

    public static boolean isServerReachable(Context context) {
        Timber.i("Checking if server is reachable: " + BuildConfig.DOMAIN);
        try {
            return InetAddress.getByName(BuildConfig.DOMAIN).isReachable(5000);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Use this method to check the network's availability
     *
     * @param context The context where checking is executed
     * @return true if network is available, false if it's not
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) return false;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }
}
