package ai.elimu.appstore

import ai.elimu.appstore.util.SharedPreferencesHelper
import ai.elimu.appstore.util.VersionHelper
import android.app.Application
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import timber.log.Timber.Forest.plant

class BaseApplication : Application() {

    override fun onCreate() {
        Timber.tag(javaClass.name).i("onCreate")
        super.onCreate()

        // Log config 🪵
        plant(Timber.DebugTree())
        Timber.i("onCreate")

        VersionHelper.updateAppVersion(applicationContext)
    }

    val retrofit: Retrofit
        get() {
            val retrofit = Retrofit.Builder()
                .baseUrl(restUrl + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit
        }

    val baseUrl: String
        /**
         * E.g. "https://eng.elimu.ai" or "https://hin.elimu.ai"
         */
        get() {
            val language =
                SharedPreferencesHelper.getLanguage(applicationContext)
            val url = "http://" + language!!.isoCode + ".elimu.ai"
            return url
        }

    private val restUrl: String
        get() = baseUrl + "/rest/v2"
}
