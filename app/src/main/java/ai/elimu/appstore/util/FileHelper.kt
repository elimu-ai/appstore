package ai.elimu.appstore.util

import android.content.Context
import java.io.File

/**
 * Helper class for determining folder paths of APK files.
 */
object FileHelper {
    private fun getApksDirectory(context: Context): File {
        val externalFilesDir = context.getExternalFilesDir(null)
        val language = SharedPreferencesHelper.getLanguage(context)
        val languageDirectory = File(externalFilesDir, "lang-" + language!!.isoCode)
        val apksDirectory = File(languageDirectory, "apks")
        if (!apksDirectory.exists()) {
            apksDirectory.mkdirs()
        }
        return apksDirectory
    }

    @JvmStatic
    fun getApkFile(packageName: String?, versionCode: Int?, context: Context): File? {
        if ((packageName == null) || (versionCode == null)) {
            return null
        }
        val apksDirectory = getApksDirectory(context)
        val file = File(apksDirectory, "$packageName-$versionCode.apk")
        return file
    }
}
