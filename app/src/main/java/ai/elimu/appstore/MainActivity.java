package ai.elimu.appstore;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Process;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import ai.elimu.appstore.dao.ApplicationDao;
import ai.elimu.appstore.model.Application;
import ai.elimu.appstore.task.DownloadApplicationsAsyncTask;
import ai.elimu.appstore.util.ConnectivityHelper;
import ai.elimu.appstore.util.RootHelper;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 0;

    public static final String PREF_LAST_SYNCHRONIZATION = "pref_last_synchronization";

    private TextView mTextViewMain;

    private TextView mTextViewLastSynchronization;

    private Button mButtonSynchronization;

    private ProgressBar mProgressBarSynchronization;

    private ApplicationDao applicationDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(getClass().getName(), "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mTextViewMain = (TextView) findViewById(R.id.textViewMain);
        mTextViewLastSynchronization = (TextView) findViewById(R.id.textViewLastSynchronization);
        mButtonSynchronization = (Button) findViewById(R.id.buttonSynchronization);
        mProgressBarSynchronization = (ProgressBar) findViewById(R.id.progressBarSynchronization);

        AppstoreApplication appstoreApplication = (AppstoreApplication) getApplication();
        applicationDao = appstoreApplication.getDaoSession().getApplicationDao();
    }

    @Override
    protected void onStart() {
        Log.i(getClass().getName(), "onStart");
        super.onStart();

        // 1. Write permission is needed for storing Log4J files
        int permissionCheckWriteExternalStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheckWriteExternalStorage != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
            return;
        }

        // 2. Ask for root access (encourage user to select "Remember my choice")
        boolean isSuccessRoot = RootHelper.runAsRoot(new String[] {
                "echo \"Do I have root?\" >/system/sd/temporary.txt\n",
                "exit\n"
        });
        Log.i(getClass().getName(), "isSuccessRoot: " + isSuccessRoot);
        if (!isSuccessRoot) {
            finish();
            return;
        }

        // 3. Select locale
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String localeAsString = sharedPreferences.getString(LocaleActivity.PREF_LOCALE, null);
        if (TextUtils.isEmpty(localeAsString)) {
            // Ask user to select preferred Locale
            Intent localeIntent = new Intent(this, LocaleActivity.class);
            startActivity(localeIntent);
            return;
        }
        int languageResourceId = getResources().getIdentifier("language_" + localeAsString.toLowerCase(), "string", getApplicationContext().getPackageName());
        mTextViewMain.setText(getString(R.string.locale_selected) + ": " + localeAsString + " (" + getString(languageResourceId) + ")");

        // 4. Password to be used for checksum generation
        String password = sharedPreferences.getString(PasswordActivity.PREF_PASSWORD, null);
        if (TextUtils.isEmpty(password)) {
            // Ask user to type password
            Intent passwordIntent = new Intent(this, PasswordActivity.class);
            startActivity(passwordIntent);
            return;
        }

        long timeOfLastSynchronizationInMillis = sharedPreferences.getLong(PREF_LAST_SYNCHRONIZATION, 0);
        if (timeOfLastSynchronizationInMillis > 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timeOfLastSynchronizationInMillis);
            mTextViewLastSynchronization.setText(getString(R.string.last_synchronization) + ": " + calendar.getTime());
        } else {
            mTextViewLastSynchronization.setText(getString(R.string.last_synchronization) + ": " + getString(R.string.never));
        }

        mButtonSynchronization.setVisibility(View.VISIBLE);
        mButtonSynchronization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(getClass().getName(), "mButtonSynchronization onClick");

                mTextViewLastSynchronization.setText(getString(R.string.synchronizing) + "...");
                mButtonSynchronization.setVisibility(View.GONE);
                mProgressBarSynchronization.setVisibility(View.VISIBLE);

                boolean isWifiEnabled = ConnectivityHelper.isWifiEnabled(getApplicationContext());
                Log.i(getClass().getName(), "isWifiEnabled: " + isWifiEnabled);
                boolean isWifiConnected = ConnectivityHelper.isWifiConnected(getApplicationContext());
                Log.i(getClass().getName(), "isWifiConnected: " + isWifiConnected);
                if (!isWifiEnabled) {
                    Toast.makeText(getApplicationContext(), getString(R.string.wifi_needs_to_be_enabled), Toast.LENGTH_SHORT).show();
                } else if (!isWifiConnected) {
                    Toast.makeText(getApplicationContext(), getString(R.string.wifi_needs_to_be_connected), Toast.LENGTH_SHORT).show();
                } else {
                    new DownloadApplicationsAsyncTask(getApplicationContext()).execute();
                }
            }
        });

        // Fetch list of Applications already stored in database
        List<Application> applications = applicationDao.loadAll();
        Log.i(getClass().getName(), "applications.size(): " + applications.size());
        for (Application application : applications) {
            Log.i(getClass().getName(), "id: " + application.getId() +
                    ", locale: " + application.getLocale() +
                    ", packageName: " + application.getPackageName() +
                    ", literacySkills: " + application.getLiteracySkills() +
                    ", numeracySkills: " + application.getNumeracySkills() +
                    ", applicationStatus: " + application.getApplicationStatus() +
                    ", versionCode: " + application.getVersionCode()
            );
        }
        // TODO: display the list of Applications in UI
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted
            } else {
                // Permission denied
            }
        }

        // Close application completely (in order to trigger Log4J configuration in AppstoreApplication on next start)
        finish();
        Process.killProcess(Process.myPid());
        System.exit(0);
    }
}
