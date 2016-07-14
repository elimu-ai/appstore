package org.literacyapp.appstore;

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
import android.widget.TextView;

import org.apache.log4j.Logger;
import org.literacyapp.appstore.service.SynchronizationService;

public class MainActivity extends AppCompatActivity {

    public static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 0;

    private Logger logger = Logger.getLogger(getClass());

    private TextView mTextViewMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        logger.info("onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mTextViewMain = (TextView) findViewById(R.id.textViewMain);
    }

    @Override
    protected void onStart() {
        logger.info("onStart");
        super.onStart();

        // 1. Write permission is needed for storing Log4J files
        int permissionCheckWriteExternalStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheckWriteExternalStorage != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
            return;
        }

        // 2. Select locale
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String localeAsString = sharedPreferences.getString(LocaleActivity.PREF_LOCALE, null);
        if (TextUtils.isEmpty(localeAsString)) {
            // Ask user to select preferred Locale
            Intent localeIntent = new Intent(this, LocaleActivity.class);
            startActivity(localeIntent);
            return;
        }
        mTextViewMain.setText("Locale selected: " + localeAsString);

        // 3. Start service for synchronizing applications
        Intent synchronizationServiceIntent = new Intent(this, SynchronizationService.class);
        startService(synchronizationServiceIntent);
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
