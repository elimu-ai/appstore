package ai.elimu.appstore.util

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.pm.PackageInfoCompat
import timber.log.Timber

object InstallationHelper {
    /**
     * Checks if an APK file has been installed on the device or not.
     */
    @JvmStatic
    fun isApplicationInstalled(packageName: String, context: Context): Boolean {
        val packageManager = context.packageManager
        try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        } catch (e: PackageManager.NameNotFoundException) {
            Timber.e(e)
            return false
        }
        return true
    }

    /**
     * Gets the `versionCode` of an APK that has already been installed on the device.
     *
     *
     *
     * Returns `0` if no APK with the `packageName` has been installed on the device.
     */
    @JvmStatic
    fun getVersionCodeOfInstalledApplication(packageName: String, context: Context): Int {
        try {
            val packageInfo = context.packageManager.getPackageInfo(packageName, 0)
            val longVersionCode = PackageInfoCompat.getLongVersionCode(packageInfo)
            val versionCode = longVersionCode.toInt()
            return versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            Timber.e(e)
            return 0
        }
    }
}
