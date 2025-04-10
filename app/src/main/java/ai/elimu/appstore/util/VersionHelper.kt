package ai.elimu.appstore.util

import android.content.Context
import android.content.pm.PackageManager
import timber.log.Timber

/**
 * Helps detect upgrades from previously installed versions of the app.
 */
object VersionHelper {
    /**
     * @return Application's version code from the `PackageManager`.
     */
    private fun getAppVersionCode(context: Context): Int {
        Timber.i("getAppVersionCode")

        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            return packageInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            throw RuntimeException("Could not get package name: $e")
        }
    }

    /**
     * Stores the version code of the application currently installed. And detects upgrades from previously installed
     * versions.
     */
    fun updateAppVersion(context: Context) {
        Timber.i("updateAppVersion")

        // Check if the application's versionCode was upgraded
        var oldVersionCode = SharedPreferencesHelper.getAppVersionCode(context)
        val newVersionCode = getAppVersionCode(context)
        if (oldVersionCode == 0) {
            SharedPreferencesHelper.storeAppVersionCode(context, newVersionCode)
            oldVersionCode = newVersionCode
        }
        Timber.i("oldVersionCode: $oldVersionCode")
        Timber.i("newVersionCode: $newVersionCode")

        // Handle upgrade from previous version
        if (oldVersionCode < newVersionCode) {
            Timber.i("Upgrading application from version $oldVersionCode to $newVersionCode...")

            //            if (oldVersionCode < ???) {
//                ...
//            }
            SharedPreferencesHelper.storeAppVersionCode(context, newVersionCode)
        }
    }
}
