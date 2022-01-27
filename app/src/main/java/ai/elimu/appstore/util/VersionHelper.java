package ai.elimu.appstore.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import timber.log.Timber;

/**
 * Helps detect upgrades from previously installed versions of the app.
 */
public class VersionHelper {

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    public static int getAppVersionCode(Context context) {
        Timber.i("getAppVersionCode");

        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Stores the versionCode of the application currently installed. And detects upgrades from previously installed
     * versions.
     */
    public static void updateAppVersion(Context context) {
        Timber.i("updateAppVersion");

        // Check if the application's versionCode was upgraded
        int oldVersionCode = SharedPreferencesHelper.getAppVersionCode(context);
        int newVersionCode = VersionHelper.getAppVersionCode(context);
        if (oldVersionCode == 0) {
            SharedPreferencesHelper.storeAppVersionCode(context, newVersionCode);
            oldVersionCode = newVersionCode;
        }
        Timber.i("oldVersionCode: " + oldVersionCode);
        Timber.i("newVersionCode: " + newVersionCode);

        // Handle upgrade from previous version
        if (oldVersionCode < newVersionCode) {
            Timber.i("Upgrading application from version " + oldVersionCode + " to " + newVersionCode + "...");

            if (oldVersionCode < 2003000) {
                // Clear all stored preferences
                SharedPreferencesHelper.clearAllPreferences(context);
            }

//            if (oldVersionCode < ???) {
//                ...
//            }

            SharedPreferencesHelper.storeAppVersionCode(context, newVersionCode);
        }
    }
}
