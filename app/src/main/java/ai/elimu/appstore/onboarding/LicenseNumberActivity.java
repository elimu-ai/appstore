package ai.elimu.appstore.onboarding;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

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
    public static final String PREF_APP_COLLECTION_ID = "pref_app_collection_id";

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
                                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    sharedPreferences.edit().putString(PREF_LICENSE_EMAIL, licenseEmail).commit();
                                    sharedPreferences.edit().putString(PREF_LICENSE_NUMBER, licenseNumber).commit();
                                    sharedPreferences.edit().putLong(PREF_APP_COLLECTION_ID, appCollectionId).commit();

                                    // Redirect user to AppListActivity
                                    // TODO
                                } else {
                                    // TODO: display error message
                                }
                            } catch (IOException | JSONException e) {
                                Timber.e(e);

                                // TODO: display error message
                            }
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
        Timber.i("updateSubmitButton");

        if (TextUtils.isEmpty(editTextLicenseEmail.getText().toString())
                || TextUtils.isEmpty(editTextLicenseNumber.getText().toString())) {
            buttonLicenseNumber.setEnabled(false);
        } else {
            buttonLicenseNumber.setEnabled(true);
        }
    }
}
