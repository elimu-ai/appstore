package ai.elimu.appstore.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import androidx.core.content.pm.PackageInfoCompat;

public class InstallationHelper {

    /**
     * Checks if an APK file has been installed on the device or not.
     */
    public static boolean isApplicationInstalled(String packageName, Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    /**
     * Gets the {@code versionCode} of an APK that has already been installed on the device.
     * <p />
     *
     * Returns {@code 0} if no APK with the {@code packageName} has been installed on the device.
     */
    public static int getVersionCodeOfInstalledApplication(String packageName, Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            long longVersionCode = PackageInfoCompat.getLongVersionCode(packageInfo);
            int versionCode = (int) longVersionCode;
            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }
}
