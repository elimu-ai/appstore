package org.literacyapp.appstore.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class DeviceInfoHelper {

    private static Logger logger = Logger.getLogger(DeviceInfoHelper.class);

    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getDeviceModel(Context context) {
        String deviceModel = "";
        try {
            deviceModel = URLEncoder.encode(Build.MODEL, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("Build.MODEL: " + Build.MODEL, e);
        }
        return deviceModel;
    }

    public static String getApplicationId(Context context) {
        return context.getPackageName();
    }

    public static int getAppVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            logger.error("Could not get package name", e);
            return -1;
        }
    }
}
