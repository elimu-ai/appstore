package ai.elimu.appstore.presentation.onboarding.validatelicense;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ai.elimu.appstore.R;
import ai.elimu.appstore.domain.model.LicenseValidationResponse;
import ai.elimu.appstore.mvpcore.Injection;
import ai.elimu.appstore.presentation.common.BaseActivity;
import ai.elimu.appstore.presentation.onboarding.DeviceRegistrationActivity;
import ai.elimu.appstore.synchronization.AppSynchronizationActivity;
import ai.elimu.appstore.util.Preconditions;
import ai.elimu.appstore.util.SharedPreferenceManager;
import timber.log.Timber;

public class LicenseNumberActivity extends BaseActivity implements ValidateLicenseContract.View {

    public static final String PREF_LICENSE_EMAIL = "pref_license_email";
    public static final String PREF_LICENSE_NUMBER = "pref_license_number";

    private EditText editTextLicenseEmail;

    private EditText editTextLicenseNumber;

    private Button buttonLicenseNumber;

    private ValidateLicenseContract.Presenter validateLicensePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license_number);

        editTextLicenseEmail = findViewById(R.id.editTextLicenseEmail);
        editTextLicenseNumber = findViewById(R.id.editTextLicenseNumber);
        buttonLicenseNumber = findViewById(R.id.buttonLicenseNumber);

        /**
         * Create presenter for validating license
         */
        new ValidateLicensePresenter(this,
                Injection.provideUseCaseHandler(),
                Injection.provideValidateLicense(getApplicationContext()));
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

                    /**
                     * submit to REST API for validation
                     */
                    validateLicensePresenter.validateLicense(licenseEmail, licenseNumber);
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

    @Override
    public void setLoading(boolean isActive) {
        setLoadingDialog(isActive);
    }

    @Override
    public void showValidateLicenseSuccess(@NonNull LicenseValidationResponse
                                                   licenseValidationResponse) {
        setLoading(false);

        /**
         * Store license email, license number and appCollectionId in shared preferences
         */
        String licenseEmail = editTextLicenseEmail.getText().toString();
        String licenseNumber = editTextLicenseNumber.getText().toString();
        SharedPreferenceManager.saveLicenseEmail(licenseEmail);
        SharedPreferenceManager.saveLicenseNumber(licenseNumber);
        SharedPreferenceManager.saveAppCollectionId(licenseValidationResponse.getAppCollectionId());

        // TODO: if valid license number, fetch and store locale of app collection, and redirect
        // to download activity

        /**
         * Check if device is registered and redirect to suitable screen
         */
        // Register device
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRegistered = sharedPreferences.getBoolean(DeviceRegistrationActivity
                .PREF_IS_REGISTERED, false);
        Timber.i("isRegistered: " + isRegistered);
        if (!isRegistered) {
            Intent intent = new Intent(this, DeviceRegistrationActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Synchronize with list of apps stored on server
            Intent intent = new Intent(this, AppSynchronizationActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void showValidateLicenseFail(int errorCode) {
        setLoading(false);

        /**
         * Display error message in case of validation failure
         */
        Toast.makeText(this,
                getString(R.string.license_validation_wrong_license),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setPresenter(ValidateLicenseContract.Presenter presenter) {
        validateLicensePresenter = Preconditions.checkNotNull(presenter);
    }
}
