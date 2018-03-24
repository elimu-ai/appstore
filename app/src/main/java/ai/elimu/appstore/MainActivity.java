package ai.elimu.appstore;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;

import ai.elimu.appstore.onboarding.DeviceRegistrationActivity;
import ai.elimu.appstore.onboarding.LicenseNumberActivity;
import ai.elimu.appstore.onboarding.LocaleActivity;
import ai.elimu.appstore.synchronization.AppSynchronizationActivity;
import ai.elimu.appstore.util.AppPrefs;
import ai.elimu.appstore.util.RootUtil;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    public static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i("onCreate");
        super.onCreate(savedInstanceState);

        //Initialize Crashlytics for crash reporting
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        Timber.i("onStart");
        super.onStart();


        // Ask for write permission (needed for downloading APK files to SD card)
        int permissionCheckWriteExternalStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheckWriteExternalStorage != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
            return;
        }

        // Ask for root access (to automate app installations)
        boolean isDeviceRooted = RootUtil.isDeviceRooted();
        Timber.i("isDeviceRooted: " + isDeviceRooted);
        if (isDeviceRooted) {
            // Ask for root access
            // TODO
//            RootHelper.runAsRoot(new String[] {
//                    "echo \"Do I have root?\" >/system/sd/temporary.txt\n",
//                    "exit\n"
//            });

        }


        if ("elimu".equals(BuildConfig.FLAVOR)) {
            // Default app collection (elimu.ai)

            // Ask for locale (only apps for the selected locale will be downloaded)
            String localeAsString = AppPrefs.getLocale();
            Timber.i("localeAsString: " + localeAsString);
            if (TextUtils.isEmpty(localeAsString)) {
                Intent intent = new Intent(this, LocaleActivity.class);
                startActivity(intent);
                finish();
            } else {
                // Register device
                boolean isRegistered = AppPrefs.isDeviceRegistered();
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
        } else {
            // Custom app collection (custom project)

            Long appCollectionId = AppPrefs.getAppCollectionId();
            Timber.i("appCollectionId: " + appCollectionId);
            if (appCollectionId == 0) {
                Intent intent = new Intent(this, LicenseNumberActivity.class);
                startActivity(intent);
                finish();
            } else {
                // Register device
                boolean isRegistered = AppPrefs.isDeviceRegistered();
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
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Timber.i("onRequestPermissionsResult");
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
