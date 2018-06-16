package ai.elimu.appstore.onboarding.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ai.elimu.appstore.BaseApplication;
import ai.elimu.appstore.R;
import ai.elimu.appstore.rest.LicenseService;
import ai.elimu.appstore.util.AppPrefs;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class LicenseNumberActivity extends AppCompatActivity {

    private LicenseService licenseService;

    private View licenseNumberDetailsContainer;
    private EditText editTextLicenseEmail;
    private EditText editTextLicenseNumber;
    private Button buttonLicenseNumber;

    private View licenseNumberLoadingContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i("onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_license_number);

        BaseApplication baseApplication = (BaseApplication) getApplication();
        licenseService = baseApplication.getRetrofit(null).create(LicenseService.class);

        licenseNumberDetailsContainer = findViewById(R.id.licenseNumberDetailsContainer);
        editTextLicenseEmail = findViewById(R.id.editTextLicenseEmail);
        editTextLicenseNumber = findViewById(R.id.editTextLicenseNumber);
        buttonLicenseNumber = findViewById(R.id.buttonLicenseNumber);

        licenseNumberLoadingContainer = findViewById(R.id.licenseNumberLoadingContainer);
    }

    @Override
    protected void onStart() {
        Timber.i("onStart");
        super.onStart();

        editTextLicenseEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Timber.i("editTextLicenseEmail onTextChanged");

                updateSubmitButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editTextLicenseNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Timber.i("editTextLicenseNumber onTextChanged");

                if ((charSequence.length() == 4)
                        || (charSequence.length() == (8+1))
                        || (charSequence.length() == (12+2))) {
                    // Prepend "-" automatically to make it easier for the user to type the license number
                    editTextLicenseNumber.setText(charSequence + "-");
                    editTextLicenseNumber.setSelection(editTextLicenseNumber.getText().length());
                }

                updateSubmitButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        buttonLicenseNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.i("onClick");

                licenseNumberDetailsContainer.setVisibility(View.GONE);
                licenseNumberLoadingContainer.setVisibility(View.VISIBLE);

                final String licenseEmail = editTextLicenseEmail.getText().toString();
                final String licenseNumber = editTextLicenseNumber.getText().toString();
                if (!TextUtils.isEmpty(licenseEmail) && !TextUtils.isEmpty(licenseNumber)) {
                    // Submit License details to REST API for validation
                    Call<ResponseBody> call = licenseService.getLicense(licenseEmail, licenseNumber);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Timber.i("onResponse");

                            try {
                                String jsonBody = response.body().string();
                                JSONObject jsonObject = new JSONObject(jsonBody);
                                if (jsonObject.has("appCollectionId")) {
                                    // The License submitted was valid

                                    Long appCollectionId = jsonObject.getLong("appCollectionId");
                                    Timber.i("appCollectionId: " + appCollectionId);

                                    // Store details in SharedPreferences
                                    AppPrefs.saveLicenseEmail(licenseEmail);
                                    AppPrefs.saveLicenseNumber(licenseNumber);
                                    AppPrefs.saveAppCollectionId(appCollectionId);

                                    // Restart application
                                    Intent intent = getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), R.string.license_validation_failed, Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException | JSONException e) {
                                Timber.e(e);

                                Toast.makeText(getApplicationContext(), "License validation failed", Toast.LENGTH_SHORT).show();
                            }

                            licenseNumberDetailsContainer.setVisibility(View.VISIBLE);
                            licenseNumberLoadingContainer.setVisibility(View.GONE);
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Timber.e(t, "onFailure");

                            Toast.makeText(getApplicationContext(), "License validation failed", Toast.LENGTH_SHORT).show();

                            licenseNumberDetailsContainer.setVisibility(View.VISIBLE);
                            licenseNumberLoadingContainer.setVisibility(View.GONE);
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
        Timber.i("updateSubmitButton");

        if (TextUtils.isEmpty(editTextLicenseEmail.getText().toString())
                || TextUtils.isEmpty(editTextLicenseNumber.getText().toString())) {
            buttonLicenseNumber.setEnabled(false);
        } else {
            buttonLicenseNumber.setEnabled(true);
        }
    }
}
