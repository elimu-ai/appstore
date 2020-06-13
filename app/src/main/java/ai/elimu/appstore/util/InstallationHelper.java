package ai.elimu.appstore.util;

import android.content.Context;
import android.content.pm.PackageManager;

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
}
