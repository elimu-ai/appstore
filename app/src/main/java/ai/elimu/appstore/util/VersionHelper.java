package ai.elimu.appstore.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.io.File;

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
     * Stores the versionCode of the application currently installed. And detects upgrades from previously installed
     * versions.
     */
    public static void updateAppVersion (Context context) {
        Timber.i("updateAppVersion");

        // Check if the application's versionCode was upgraded
        int oldVersionCode = AppPrefs.getAppVersionCode();
        int newVersionCode = VersionHelper.getAppVersionCode(context);
        if (oldVersionCode == 0) {
            AppPrefs.saveAppVersionCode(newVersionCode);
            oldVersionCode = newVersionCode;
        }
        Timber.i("oldVersionCode: " + oldVersionCode);
        Timber.i("newVersionCode: " + newVersionCode);

        // Handle upgrade from previous version
        if (oldVersionCode < newVersionCode) {
            Timber.i("Upgrading application from version " + oldVersionCode + " to " + newVersionCode + "...");

            if (oldVersionCode < 2000011) {
                // Delete downloaded APK files from SD card to prevent testers from having to manually delete corrupt files
                File apkDirectory = new File(Environment.getExternalStorageDirectory() + "/.elimu-ai/appstore/apks/en/");
                for (File apkFile : apkDirectory.listFiles()) {
                    Timber.i("Deleted " + apkFile + ": " + apkFile.delete());
                }
            }

            if (oldVersionCode < 2000018) {
                // Delete downloaded APK files from SD card to prevent testers from having to manually delete corrupt files
                File apkDirectory = new File(Environment.getExternalStorageDirectory() + "/.elimu-ai/appstore/apks/en/");
                for (File apkFile : apkDirectory.listFiles()) {
                    Timber.i("Deleted " + apkFile + ": " + apkFile.delete());
                }
            }

//            if (oldVersionCode < 2000019) {
//                ...
//            }

            AppPrefs.saveAppVersionCode(newVersionCode);
        }
    }
}
