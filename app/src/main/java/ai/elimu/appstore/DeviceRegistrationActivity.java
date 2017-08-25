package ai.elimu.appstore;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import ai.elimu.appstore.util.ConnectivityHelper;
import ai.elimu.appstore.util.DeviceInfoHelper;
import ai.elimu.appstore.util.JsonLoader;
import ai.elimu.appstore.util.UserPrefsHelper;
import ai.elimu.appstore.util.VersionHelper;
import timber.log.Timber;

public class DeviceRegistrationActivity extends AppCompatActivity {

    public static final String PREF_IS_REGISTERED = "pref_is_registered";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i("onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_device_registration);
    }

    @Override
    protected void onStart() {
        Timber.i("onStart");
        super.onStart();

        new RegisterDeviceAsyncTask(this).execute();
    }


    public class RegisterDeviceAsyncTask extends AsyncTask<Void, Void, String> {

        private Context context;

        public RegisterDeviceAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... voids) {
            Timber.i("doInBackground");

            boolean isServerReachable = ConnectivityHelper.isServerReachable(context);
            Timber.i("isServerReachable: " + isServerReachable);
            if (!isServerReachable) {
                return null;
            } else {
                String url = BuildConfig.REST_URL + "/device/create" +
                        "?deviceId=" + DeviceInfoHelper.getDeviceId(context) +
                        "&deviceManufacturer=" + DeviceInfoHelper.getDeviceManufacturer(context) +
                        "&deviceModel=" + DeviceInfoHelper.getDeviceModel(context) +
                        "&deviceSerial=" + DeviceInfoHelper.getDeviceSerialNumber(context) +
                        "&applicationId=" + context.getPackageName() +
                        "&appVersionCode=" + VersionHelper.getAppVersionCode(context) +
                        "&osVersion=" + Build.VERSION.SDK_INT +
                        "&locale=" + UserPrefsHelper.getLocale(context);
                String jsonResponse = JsonLoader.loadJson(url);
                Timber.i("jsonResponse: " + jsonResponse);
                return jsonResponse;
            }
        }

        @Override
        protected void onPostExecute(String jsonResponse) {
            Timber.i("onPostExecute");
            super.onPostExecute(jsonResponse);

            if (TextUtils.isEmpty(jsonResponse)) {
                Toast.makeText(context, context.getString(R.string.server_is_not_reachable), Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    if ("success".equals(jsonObject.getString("result"))) {
                        // Device was successfully registered
                        Timber.i("Device was successfully registered");

                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        sharedPreferences.edit().putBoolean(PREF_IS_REGISTERED, true).commit();

                        // Restart application
                        Intent intent = getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else if ("error".equals(jsonObject.getString("result")) && "Device already exists".equals(jsonObject.getString("description"))) {
                        // Device has already been registered
                        Timber.i("Device has already been registered");

                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        sharedPreferences.edit().putBoolean(PREF_IS_REGISTERED, true).commit();

                        // Restart application
                        Intent intent = getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        // Device registration failed
                        Toast.makeText(context, context.getString(R.string.device_registration_failed), Toast.LENGTH_SHORT).show();
                        Log.w(getClass().getName(), context.getString(R.string.device_registration_failed) + ": " + jsonObject.getString("description"));
                    }
                } catch (JSONException e) {
                    Log.e(getClass().getName(), null, e);
                    Toast.makeText(context, "Error: " + e, Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
