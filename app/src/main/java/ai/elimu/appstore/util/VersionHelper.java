package ai.elimu.appstore.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class VersionHelper {

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    public static int getAppVersionCode(Context context) {
        Log.i(VersionHelper.class.getName(), "getAppVersionCode");

        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
}
