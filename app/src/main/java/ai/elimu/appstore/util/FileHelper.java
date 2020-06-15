package ai.elimu.appstore.util;

import android.content.Context;

import java.io.File;

import ai.elimu.model.enums.Language;

/**
 * Helper class for determining folder paths of APK files.
 */
public class FileHelper {

    private static File getApksDirectory(Context context) {
        File externalFilesDir = context.getExternalFilesDir(null);
        Language language = SharedPreferencesHelper.getLanguage(context);
        File languageDirectory = new File(externalFilesDir, "lang-" + language.getIsoCode());
        File apksDirectory = new File(languageDirectory, "apks");
        if (!apksDirectory.exists()) {
            apksDirectory.mkdirs();
        }
        return apksDirectory;
    }

    public static File getApkFile(String packageName, Integer versionCode, Context context) {
        if ((packageName == null) || (versionCode == null)) {
            return null;
        }
        File apksDirectory = getApksDirectory(context);
        File file = new File(apksDirectory, packageName + "-" + versionCode + ".apk");
        return file;
    }
}
