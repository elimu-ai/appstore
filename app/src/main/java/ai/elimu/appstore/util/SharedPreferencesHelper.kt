package ai.elimu.appstore.util

import ai.elimu.model.v2.enums.Language
import android.content.Context
import android.text.TextUtils
import timber.log.Timber
import androidx.core.content.edit

object SharedPreferencesHelper {
    private const val SHARED_PREFS = "shared_prefs"

    private const val PREF_APP_VERSION_CODE: String = "pref_app_version_code"
    private const val PREF_LANGUAGE: String = "pref_language"

    @JvmStatic
    fun storeAppVersionCode(context: Context, appVersionCode: Int) {
        Timber.i("storeAppVersionCode")
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        sharedPreferences.edit { putInt(PREF_APP_VERSION_CODE, appVersionCode) }
    }

    @JvmStatic
    fun getAppVersionCode(context: Context): Int {
        Timber.i("getAppVersionCode")
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(PREF_APP_VERSION_CODE, 0)
    }


    @JvmStatic
    fun storeLanguage(context: Context, language: Language) {
        Timber.i("storeLanguage")
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        sharedPreferences.edit { putString(PREF_LANGUAGE, language.toString()) }
    }

    @JvmStatic
    fun getLanguage(context: Context): Language? {
        Timber.i("getLanguage")
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val languageAsString = sharedPreferences.getString(PREF_LANGUAGE, null)
        if (TextUtils.isEmpty(languageAsString)) {
            return null
        } else {
            val language = Language.valueOf(
                languageAsString!!
            )
            return language
        }
    }
}
