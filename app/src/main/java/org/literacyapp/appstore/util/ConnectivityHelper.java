package org.literacyapp.appstore.util;

import android.content.Context;
import android.net.wifi.WifiManager;

import java.io.IOException;
import java.net.InetAddress;

public class ConnectivityHelper {

    public static boolean isWifiEnabled(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    public static boolean isServerReachable(Context context) {
        try {
            return InetAddress.getByName(EnvironmentSettings.DOMAIN).isReachable(5000);
        } catch (IOException e) {
            return false;
        }
    }
}
