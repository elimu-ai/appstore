package ai.elimu.appstore.ui

import ai.elimu.appstore.R
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

class SelectLanguageActivity : AppCompatActivity() {
    
    private val TAG = javaClass.name
    
    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.tag(TAG).i("onCreate")
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_select_language)
    }

    override fun onStart() {
        Timber.tag(TAG).i("onStart")
        super.onStart()

        LanguageListDialogFragment.newInstance().show(supportFragmentManager, "dialog")
    }
}
