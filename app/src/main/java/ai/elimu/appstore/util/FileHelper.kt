package ai.elimu.appstore.util

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

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

    suspend fun calculateMD5Checksum(filePath: String): String = withContext(Dispatchers.IO) {
        try {
            val buffer = ByteArray(4 * 1024)
            val md = MessageDigest.getInstance("MD5")
            FileInputStream(File(filePath)).use { input ->
                var bytesRead: Int
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    md.update(buffer, 0, bytesRead)
                }
            }
            val digest = md.digest()
            digest.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            Timber.e(e)
            ""
        }
    }
}
