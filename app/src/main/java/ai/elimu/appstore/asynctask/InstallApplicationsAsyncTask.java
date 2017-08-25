package ai.elimu.appstore.asynctask;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import ai.elimu.appstore.BaseApplication;
import ai.elimu.appstore.dao.ApplicationDao;
import ai.elimu.appstore.model.Application;
import ai.elimu.appstore.util.ApkLoader;
import ai.elimu.appstore.util.DeviceInfoHelper;
import ai.elimu.model.enums.admin.ApplicationStatus;
import timber.log.Timber;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;

public class InstallApplicationsAsyncTask extends AsyncTask<Object, String, Void> {

    private Context context;

    private ApplicationDao applicationDao;

    public InstallApplicationsAsyncTask(Context context) {
        this.context = context;

        BaseApplication baseApplication = (BaseApplication) context;
        applicationDao = baseApplication.getDaoSession().getApplicationDao();
    }

    @Override
    protected Void doInBackground(Object... objects) {
        Timber.i("doInBackground");

        List<Application> applications = applicationDao.loadAll();
        for (Application application : applications) {
            PackageManager packageManager = context.getPackageManager();
            if (application.getApplicationStatus() == ApplicationStatus.ACTIVE) {
                try {
                    PackageInfo packageInfo = packageManager.getPackageInfo(application.getPackageName(), PackageManager.GET_ACTIVITIES);
                } catch (PackageManager.NameNotFoundException e) {
                    Timber.i("The application is not installed: " + application.getPackageName());
//                    ApplicationVersion applicationVersion = application.getApplicationVersions().get(0);
//                    installApk(applicationVersion);
                }
            }
        }

        return null;
    }

    private void installApk(Application application) {
        Timber.i("installApk");

        String fileName = application.getPackageName() + "-" + application.getVersionCode() + ".apk";
        Timber.i("fileName: " + fileName);

        String language = Locale.getDefault().getLanguage();
        File apkDirectory = new File(Environment.getExternalStorageDirectory() + "/.elimu-ai/appstore/apks/" + language);
        Log.i(ApkLoader.class.getName(), "apkDirectory: " + apkDirectory);
        if (!apkDirectory.exists()) {
            apkDirectory.mkdirs();
        }

        File apkFile = new File(apkDirectory, fileName);
        Log.i(ApkLoader.class.getName(), "apkFile: " + apkFile);
        Log.i(ApkLoader.class.getName(), "apkFile.exists(): " + apkFile.exists());

        if ((apkFile == null) || !apkFile.exists()) {
            Log.w(getClass().getName(), "APK installation failed: " + apkFile);
        } else {
            Timber.i("Installing APK: " + application.getPackageName() + " (version " + application.getVersionCode() + ")");
            String command = "pm install -r -g " + apkFile.getAbsolutePath(); // https://developer.android.com/studio/command-line/shell.html#pm
            if ("KFFOWI".equals(DeviceInfoHelper.getDeviceModel(context))) {
                // The '-g' command does not work on Amazon Fire: "Error: Unknown option: -g"
                command = "pm install -r " + apkFile.getAbsolutePath();
            }
            Timber.i("command: " + command);
            try {
                Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
                process.waitFor();

                InputStream inputStreamSuccess = process.getInputStream();
                if (inputStreamSuccess != null) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStreamSuccess));
                    String successMessage = bufferedReader.readLine();
                    Timber.i("successMessage: " + successMessage);
                    if (!"Success".equals(successMessage)) {
                        Timber.i("APK installation failed: " + application.getPackageName() + " (version " + application.getVersionCode() + ")");
                    }

                    String startCommand = application.getStartCommand();
                    if (!TextUtils.isEmpty(startCommand)) {
                        // Expected format: "adb shell <startCommand>"
                        Timber.i("startCommand: " + startCommand);

                        process = Runtime.getRuntime().exec(new String[]{"su", "-c", startCommand});
                        process.waitFor();

                        inputStreamSuccess = process.getInputStream();
                        if (inputStreamSuccess != null) {
                            bufferedReader = new BufferedReader(new InputStreamReader(inputStreamSuccess));
                            successMessage = bufferedReader.readLine();
                            Timber.i("startCommand successMessage: " + successMessage);
                        }

                        InputStream inputStreamError = process.getErrorStream();
                        if (inputStreamError != null) {
                            bufferedReader = new BufferedReader(new InputStreamReader(inputStreamError));
                            String errorMessage = bufferedReader.readLine();
                            Log.w(getClass().getName(), "startCommand errorMessage: " + errorMessage);
                        }
                    }
                }

                InputStream inputStreamError = process.getErrorStream();
                if (inputStreamError != null) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStreamError));
                    String errorMessage = bufferedReader.readLine();
                    Log.w(getClass().getName(), "errorMessage: " + errorMessage);
                }
            } catch (IOException e) {
                Log.e(getClass().getName(), "IOException: " + command, e);
            } catch (InterruptedException e) {
                Log.e(getClass().getName(), "InterruptedException: " + command, e);
            }
        }
    }

    @Override
    protected void onPostExecute(Void v) {
        Timber.i("onPostExecute");
        super.onPostExecute(v);
    }
}
