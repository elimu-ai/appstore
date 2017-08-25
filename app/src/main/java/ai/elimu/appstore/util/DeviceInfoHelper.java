package ai.elimu.appstore.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import timber.log.Timber;

public class DeviceInfoHelper {

    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getDeviceManufacturer(Context context) {
        String deviceManufacturer = "";
        try {
            deviceManufacturer = URLEncoder.encode(Build.MANUFACTURER, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Timber.e(e, "Build.MANUFACTURER: " + Build.MANUFACTURER);
        }
        return deviceManufacturer;
    }

    public static String getDeviceModel(Context context) {
        String deviceModel = "";
        try {
            deviceModel = URLEncoder.encode(Build.MODEL, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Timber.d(e, "Build.MODEL: " + Build.MODEL);
        }
        return deviceModel;
    }

    public static String getDeviceSerialNumber(Context context) {
        String deviceSerial = "";
        try {
            deviceSerial = URLEncoder.encode(Build.SERIAL, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Timber.d(e, "Build.SERIAL: " + Build.SERIAL);
        }
        return deviceSerial;
    }

    public static String getApplicationId(Context context) {
        return context.getPackageName();
    }

    public static int getAppVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Timber.d(e, "Could not get package name");
            return -1;
        }
    }
}
