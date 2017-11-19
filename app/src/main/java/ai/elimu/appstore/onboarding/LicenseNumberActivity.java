package ai.elimu.appstore.onboarding;

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

import ai.elimu.appstore.BaseApplication;
import ai.elimu.appstore.R;
import ai.elimu.appstore.service.LicenseService;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class LicenseNumberActivity extends AppCompatActivity {

    public static final String PREF_LICENSE_EMAIL = "pref_license_email";
    public static final String PREF_LICENSE_NUMBER = "pref_license_number";

    private LicenseService licenseService;

    private EditText editTextLicenseEmail;

    private EditText editTextLicenseNumber;

    private Button buttonLicenseNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i("onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_license_number);

        BaseApplication baseApplication = (BaseApplication) getApplication();
        licenseService = baseApplication.getRetrofit().create(LicenseService.class);

        editTextLicenseEmail = findViewById(R.id.editTextLicenseEmail);
        editTextLicenseNumber = findViewById(R.id.editTextLicenseNumber);
        buttonLicenseNumber = findViewById(R.id.buttonLicenseNumber);
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
                    // Submit License details to REST API for validation
                    Call<ResponseBody> call = licenseService.getLicense(licenseEmail, licenseNumber);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Timber.i("onResponse");

                            // TODO: if valid license number, store e-mail and license number in shared preferences
                            // TODO: redirect user to AppListActivity
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Timber.e(t, "onFailure");

                            // TODO: display error message
                        }
                    });
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
