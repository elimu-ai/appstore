package ai.elimu.appstore.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import ai.elimu.appstore.R;
import ai.elimu.appstore.util.AppPrefs;
import timber.log.Timber;

public class LicenseOptionActivity extends AppCompatActivity {

    private Button buttonOptionNo;

    private Button buttonOptionYes;

    private final String LICENSE_OPTION_NO = "no";

    private final String LICENSE_OPTION_YES = "yes";

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

        buttonOptionNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.i("buttonOptionNo onClick");

                AppPrefs.saveLicenseOption(LICENSE_OPTION_NO);

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

                AppPrefs.saveLicenseOption(LICENSE_OPTION_YES);

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
