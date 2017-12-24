package ai.elimu.appstore.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import timber.log.Timber;

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
     * Update application version in case its outdated
     * preferences
     * @param context The application context
     */
    public static void updateAppVersion (@NonNull Context context) {

        // Check if the application's versionCode was upgraded
        int oldVersionCode = AppPrefs.getAppVersionCode();
        int newVersionCode = VersionHelper.getAppVersionCode(context);
        if (oldVersionCode == 0) {
            AppPrefs.saveAppVersionCode(newVersionCode);
            oldVersionCode = newVersionCode;
        }
        if (oldVersionCode < newVersionCode) {
            Timber.i("Upgrading application from version " + oldVersionCode + " to " +
                    newVersionCode);
//            if (newVersionCode == ???) {
//                // Put relevant tasks required for upgrading here
//            }
            AppPrefs.saveAppVersionCode(newVersionCode);
        }
    }
}
