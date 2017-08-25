package ai.elimu.appstore;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.w3c.dom.Text;

import timber.log.Timber;

public class LicenseNumberActivity extends AppCompatActivity {

    public static final String PREF_LICENSE_EMAIL = "pref_license_email";
    public static final String PREF_LICENSE_NUMBER = "pref_license_number";

    private EditText editTextLicenseEmail;

    private EditText editTextLicenseNumber;

    private Button buttonLicenseNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license_number);

        editTextLicenseEmail = (EditText) findViewById(R.id.editTextLicenseEmail);
        editTextLicenseNumber = (EditText) findViewById(R.id.editTextLicenseNumber);
        buttonLicenseNumber = (Button) findViewById(R.id.buttonLicenseNumber);
    }

    @Override
    protected void onStart() {
        Timber.i("onStart");
        super.onStart();

        editTextLicenseEmail.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Timber.i("editTextLicenseEmail onKey");

                updateSubmitButton();

                return false;
            }
        });

        editTextLicenseNumber.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Timber.i("editTextLicenseNumber onKey");

                updateSubmitButton();

                return false;
            }
        });

        buttonLicenseNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.i("onClick");

                String licenseEmail = editTextLicenseEmail.getText().toString();
                String licenseNumber = editTextLicenseNumber.getText().toString();
                if (!TextUtils.isEmpty(licenseEmail) && !TextUtils.isEmpty(licenseNumber)) {
                    // TODO: submit to REST API for validation
                    // TODO: if invalid license number, display error message
                    // TODO: if valid license number, store e-mail and license number in shared preferences
                    // TODO: if valid license number, fetch and store locale of app collection, and redirect to download activity
                }
            }
        });
    }

    /**
     * Keep submit button disabled until all required fields have been filled
     */
    private void updateSubmitButton() {
        if (TextUtils.isEmpty(editTextLicenseEmail.getText().toString())
                || TextUtils.isEmpty(editTextLicenseNumber.getText().toString())) {
            buttonLicenseNumber.setEnabled(false);
        } else {
            buttonLicenseNumber.setEnabled(true);
        }
    }
}
