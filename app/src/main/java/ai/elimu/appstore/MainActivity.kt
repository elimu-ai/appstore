package ai.elimu.appstore

import ai.elimu.appstore.ui.SelectLanguageActivity
import ai.elimu.appstore.ui.applications.InitialSyncActivity
import ai.elimu.appstore.util.SharedPreferencesHelper
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i("onCreate")
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        Timber.i("onStart")
        super.onStart()

        val language = SharedPreferencesHelper.getLanguage(
            applicationContext
        )
        Timber.i("language: $language")
        if (language == null) {
            // Redirect to language selection
            val intent = Intent(applicationContext, SelectLanguageActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // Redirect to Activity for downloading list of Applications from REST API
            val intent = Intent(applicationContext, InitialSyncActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
