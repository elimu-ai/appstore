package org.literacyapp.appstore.task;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.log4j.Logger;
import org.literacyapp.appstore.MainActivity;
import org.literacyapp.appstore.R;
import org.literacyapp.appstore.util.ApkLoader;
import org.literacyapp.appstore.util.ConnectivityHelper;
import org.literacyapp.appstore.util.DeviceInfoHelper;
import org.literacyapp.appstore.util.EnvironmentSettings;
import org.literacyapp.appstore.util.JsonLoader;
import org.literacyapp.model.enums.Locale;
import org.literacyapp.model.json.admin.ApplicationJson;
import org.literacyapp.model.json.admin.ApplicationVersionJson;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.List;

public class DownloadApplicationsAsyncTask extends AsyncTask<Object, Integer, List<ApplicationJson>> {

    private Logger logger = Logger.getLogger(getClass());

    private Context context;

    public DownloadApplicationsAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected List<ApplicationJson> doInBackground(Object... objects) {
        logger.info("doInBackground");

        boolean isServerReachable = ConnectivityHelper.isServerReachable(context);
        logger.info("isServerReachable: " + isServerReachable);
        if (!isServerReachable) {
            logger.warn(context.getString(R.string.server_is_not_reachable));
        } else {
            // Download List of applications
            String url = EnvironmentSettings.getBaseUrl() + "/rest/admin/application/list" +
                    "?deviceId=" + DeviceInfoHelper.getDeviceId(context) +
                    //"&checksum=" + ...
                    "&locale=" + Locale.EN +
                    "&deviceModel=" + DeviceInfoHelper.getDeviceModel(context) +
                    "&osVersion=" + Build.VERSION.SDK_INT +
                    "&appVersionCode=" + DeviceInfoHelper.getAppVersionCode(context);
            String jsonResponse = JsonLoader.loadJson(url);
            logger.info("jsonResponse: " + jsonResponse);
            Type type = new TypeToken<List<ApplicationJson>>(){}.getType();
            List<ApplicationJson> applicationJsonList = new Gson().fromJson(jsonResponse, type);
            logger.info("applicationJsonList.size(): " + applicationJsonList.size());
            int counter = 0;
            for (ApplicationJson applicationJson : applicationJsonList) {
                logger.info("applicationJson.getPackageName(): " + applicationJson.getPackageName());

                ApplicationVersionJson applicationVersionJson = applicationJson.getApplicationVersionJsonList().get(0);

                // Install/update application
                PackageManager packageManager = context.getPackageManager();
                try {
                    PackageInfo packageInfo = packageManager.getPackageInfo(applicationJson.getPackageName(), PackageManager.GET_ACTIVITIES);
                    logger.info("The application is already installed: " + applicationJson.getPackageName());
                    // Check if a newer version is available for download
                    logger.info("packageInfo.versionCode: " + packageInfo.versionCode);
                    logger.info("Newest version available for download: " + applicationVersionJson.getVersionCode());
                    if (packageInfo.versionCode < applicationVersionJson.getVersionCode()) {
                        // Download the APK and install it
                        downloadAndInstallApk(applicationVersionJson);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    logger.info("The application is not installed: " + applicationJson.getPackageName());
                    // Download the APK file and install it
                    downloadAndInstallApk(applicationVersionJson);
                }

                publishProgress(++counter * 100 / applicationJsonList.size());
            }

            // Update time of last synchronization
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            sharedPreferences.edit().putLong(MainActivity.PREF_LAST_SYNCHRONIZATION, Calendar.getInstance().getTimeInMillis()).commit();
        }

        return null;
    }

    private void downloadAndInstallApk(ApplicationVersionJson applicationVersionJson) {
        logger.info("downloadAndInstallApk");

        String fileUrl = EnvironmentSettings.getBaseUrl() + applicationVersionJson.getFileUrl() +
                "?deviceId=" + DeviceInfoHelper.getDeviceId(context) +
                //"&checksum=" + ...
                "&locale=" + Locale.EN +
                "&deviceModel=" + DeviceInfoHelper.getDeviceModel(context) +
                "&osVersion=" + Build.VERSION.SDK_INT +
                "&appVersionCode=" + DeviceInfoHelper.getAppVersionCode(context);
        logger.info("fileUrl: " + fileUrl);

        String fileName = applicationVersionJson.getApplicationJson().getPackageName() + "-" + applicationVersionJson.getVersionCode() + ".apk";
        logger.info("fileName: " + fileName);

        File apkFile = ApkLoader.loadApk(fileUrl, fileName, context);
        logger.info("apkFile: " + apkFile);
        if ((apkFile == null) || !apkFile.exists()) {
            logger.error("APK download failed: " + fileUrl);
        } else {
            String command = "pm install -r " + apkFile.getAbsolutePath();
            logger.info("command: " + command);
            try {
                Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
                process.waitFor();
                // TODO: log output
            } catch (IOException e) {
                logger.error("IOException: " + command, e);
            } catch (InterruptedException e) {
                logger.error("InterruptedException: " + command, e);
            }
        }
    }

    @Override
    protected void onProgressUpdate(Integer... percentage) {
        logger.info("onProgressUpdate");
        super.onProgressUpdate(percentage);

        int percentCompleted = percentage[0];
        logger.info("percentCompleted: " + percentCompleted + "%");
        Toast.makeText(context, "percentCompleted: " + percentCompleted + "%", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(List<ApplicationJson> applicationJsonList) {
        logger.info("onPostExecute");
        super.onPostExecute(applicationJsonList);

        Toast.makeText(context, "Synchronization complete!", Toast.LENGTH_SHORT).show();
        // TODO: deactivate ProgressBar
    }
}
