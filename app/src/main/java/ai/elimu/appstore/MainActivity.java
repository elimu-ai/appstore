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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(getClass().getName(), "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        Log.i(getClass().getName(), "onStart");
        super.onStart();


        // Ask for write permission (needed for storing APK files on SD card)
        int permissionCheckWriteExternalStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheckWriteExternalStorage != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
            return;
        }


        // Ask for root access (to automate app installations)
        boolean isSuccessRoot = RootHelper.runAsRoot(new String[] {
                "echo \"Do I have root?\" >/system/sd/temporary.txt\n",
                "exit\n"
        });
        Log.i(getClass().getName(), "isSuccessRoot: " + isSuccessRoot);
        // TODO: require root permission if rooted device?


        // Ask for license number (used in custom projects)
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String licenseOption = sharedPreferences.getString(LicenseOptionActivity.PREF_LICENSE_OPTION, null);
        Log.i(getClass().getName(), "licenseOption: " + licenseOption);
        if (TextUtils.isEmpty(licenseOption)) {
            Intent intent = new Intent(this, LicenseOptionActivity.class);
            startActivity(intent);
            finish();
        } else if ("no".equals(licenseOption)) {
            Intent intent = new Intent(this, LocaleActivity.class);
            startActivity(intent);
            finish();
        } else if ("yes".equals(licenseOption)) {
            Intent intent = new Intent(this, LicenseNumberActivity.class);
            startActivity(intent);
            finish();
        }


//        // Ask for locale (only apps for the selected locale will be downloaded)
//        String localeAsString = sharedPreferences.getString(LocaleActivity.PREF_LOCALE, null);
//        if (TextUtils.isEmpty(localeAsString)) {
//            Intent intent = new Intent(this, LocaleActivity.class);
//            startActivity(intent);
//            finish();
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(getClass().getName(), "onRequestPermissionsResult");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted

                // Restart application
                Intent intent = getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                // Permission denied

                finish();
            }
        }
    }
}
