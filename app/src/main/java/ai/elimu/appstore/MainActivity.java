package ai.elimu.appstore;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ai.elimu.appstore.ui.SelectLanguageActivity;
import ai.elimu.appstore.util.SharedPreferencesHelper;
import ai.elimu.model.enums.Language;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i("onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        Timber.i("onStart");
        super.onStart();

        Language language = SharedPreferencesHelper.getLanguage(getApplicationContext());
        Timber.i("language: " + language);
        if (language == null) {
            // Redirect to language selection
            Intent intent = new Intent(getApplicationContext(), SelectLanguageActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Download list of Applications from REST API
            // TODO
        }
    }
}
