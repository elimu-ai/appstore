package ai.elimu.appstore.onboarding;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import ai.elimu.appstore.R;
import timber.log.Timber;

public class LicenseOptionActivity extends AppCompatActivity {

    public static final String PREF_LICENSE_OPTION = "pref_license_option";

    private Button buttonOptionNo;

    private Button buttonOptionYes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license_option);

        buttonOptionNo = findViewById(R.id.buttonOptionNo);
        buttonOptionYes = findViewById(R.id.buttonOptionYes);
    }

    @Override
    protected void onStart() {
        Timber.i("onStart");
        super.onStart();

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (this);

        buttonOptionNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.i("buttonOptionNo onClick");

                sharedPreferences.edit().putString(PREF_LICENSE_OPTION, "no").commit();

                // Restart application
                Intent intent = getPackageManager().getLaunchIntentForPackage(getBaseContext()
                        .getPackageName());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        buttonOptionYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.i("buttonOptionYes onClick");

                sharedPreferences.edit().putString(PREF_LICENSE_OPTION, "yes").commit();

                // Restart application
                Intent intent = getPackageManager().getLaunchIntentForPackage(getBaseContext()
                        .getPackageName());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }
}
