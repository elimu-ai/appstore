package org.literacyapp.appstore.task;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.literacyapp.appstore.MainActivity;
import org.literacyapp.appstore.R;
import org.literacyapp.appstore.util.ApkLoader;
import org.literacyapp.appstore.util.ChecksumHelper;
import org.literacyapp.appstore.util.ConnectivityHelper;
import org.literacyapp.appstore.util.DeviceInfoHelper;
import org.literacyapp.appstore.util.EnvironmentSettings;
import org.literacyapp.appstore.util.JsonLoader;
import org.literacyapp.appstore.util.UserPrefsHelper;
import org.literacyapp.model.enums.admin.ApplicationStatus;
import org.literacyapp.model.gson.admin.ApplicationGson;
import org.literacyapp.model.gson.admin.ApplicationVersionGson;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Calendar;

public class DownloadApplicationsAsyncTask extends AsyncTask<Object, String, Void> {

    private Context context;

    public DownloadApplicationsAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Object... objects) {
        Log.i(getClass().getName(), "doInBackground");

        boolean isWifiEnabled = ConnectivityHelper.isWifiEnabled(context);
        Log.i(getClass().getName(), "isWifiEnabled: " + isWifiEnabled);
        boolean isWifiConnected = ConnectivityHelper.isWifiConnected(context);
        Log.i(getClass().getName(), "isWifiConnected: " + isWifiConnected);
        boolean isServerReachable = ConnectivityHelper.isServerReachable(context);
        Log.i(getClass().getName(), "isServerReachable: " + isServerReachable);
        if (!isWifiEnabled) {
            Log.w(getClass().getName(), context.getString(R.string.wifi_needs_to_be_enabled));
        } else if (!isWifiConnected) {
            Log.w(getClass().getName(), context.getString(R.string.wifi_needs_to_be_connected));
        } else if (!isServerReachable) {
            Log.w(getClass().getName(), context.getString(R.string.server_is_not_reachable));
        } else {
            // Download List of applications
            String url = EnvironmentSettings.getBaseRestUrl() + "/admin/application/list" +
                    "?deviceId=" + DeviceInfoHelper.getDeviceId(context) +
                    "&checksum=" + ChecksumHelper.getChecksum(context) +
                    "&locale=" + UserPrefsHelper.getLocale(context) +
                    "&deviceModel=" + DeviceInfoHelper.getDeviceModel(context) +
                    "&osVersion=" + Build.VERSION.SDK_INT +
                    "&applicationId=" + DeviceInfoHelper.getApplicationId(context) +
                    "&appVersionCode=" + DeviceInfoHelper.getAppVersionCode(context);
            String jsonResponse = JsonLoader.loadJson(url);
            Log.i(getClass().getName(), "jsonResponse: " + jsonResponse);
            try {
                JSONObject jsonObject = new JSONObject(jsonResponse);
                if (!"success".equals(jsonObject.getString("result"))) {
                    Log.w(getClass().getName(), "Download failed");
                    String errorDescription = jsonObject.getString("description");
                } else {
                    JSONArray jsonArrayApplications = jsonObject.getJSONArray("applications");
                    for (int i = 0; i < jsonArrayApplications.length(); i++) {
                        Type type = new TypeToken<ApplicationGson>() {}.getType();
                        ApplicationGson applicationGson = new Gson().fromJson(jsonArrayApplications.getString(i), type);
                        Log.i(getClass().getName(), "applicationGson.getPackageName(): " + applicationGson.getPackageName() + " (" + applicationGson.getApplicationStatus() + ")");

                        ApplicationVersionGson applicationVersionGson = applicationGson.getApplicationVersions().get(0);

                        Log.i(getClass().getName(), "Synchronizing APK " + (i + 1) + "/" + jsonArrayApplications.length() + ": " + applicationGson.getPackageName() + " (version " + applicationVersionGson.getVersionCode() + ")");

                        // Delete/update/install application
                        PackageManager packageManager = context.getPackageManager();
                        try {
                            PackageInfo packageInfo = packageManager.getPackageInfo(applicationGson.getPackageName(), PackageManager.GET_ACTIVITIES);
                            Log.i(getClass().getName(), "The application is already installed: " + applicationGson.getPackageName());

                            // Check if the Application has been deleted/deactivated on the website
                            if ((applicationGson.getApplicationStatus() == ApplicationStatus.DELETED)
                                    || (applicationGson.getApplicationStatus() == ApplicationStatus.INACTIVE)) {
                                // Delete application
                                uninstallApk(applicationGson);
                            } else if (applicationGson.getApplicationStatus() == ApplicationStatus.ACTIVE) {
                                // Check if a newer version is available for download
                                Log.i(getClass().getName(), "packageInfo.versionCode: " + packageInfo.versionCode);
                                Log.i(getClass().getName(), "Newest version available for download: " + applicationVersionGson.getVersionCode());
                                if (packageInfo.versionCode < applicationVersionGson.getVersionCode()) {
                                    // Download the APK and install it
                                    downloadAndInstallApk(applicationVersionGson);
                                }
                            }
                        } catch (PackageManager.NameNotFoundException e) {
                            Log.i(getClass().getName(), "The application is not installed: " + applicationGson.getPackageName());

                            if (applicationGson.getApplicationStatus() == ApplicationStatus.ACTIVE) {
                                // Download the APK file and install it
                                downloadAndInstallApk(applicationVersionGson);
                            }
                        }
                    }
                    Log.i(getClass().getName(), "Synchronization complete!");

                    // Update time of last synchronization
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                    sharedPreferences.edit().putLong(MainActivity.PREF_LAST_SYNCHRONIZATION, Calendar.getInstance().getTimeInMillis()).commit();
                }
            } catch (JSONException e) {
                Log.e(getClass().getName(), null, e);
            }
        }

        return null;
    }

    private void downloadAndInstallApk(ApplicationVersionGson applicationVersionGson) {
        Log.i(getClass().getName(), "downloadAndInstallApk");

        String fileUrl = EnvironmentSettings.getBaseUrl() + applicationVersionGson.getFileUrl() +
                "?deviceId=" + DeviceInfoHelper.getDeviceId(context) +
                "&checksum=" + ChecksumHelper.getChecksum(context) +
                "&locale=" + UserPrefsHelper.getLocale(context) +
                "&deviceModel=" + DeviceInfoHelper.getDeviceModel(context) +
                "&osVersion=" + Build.VERSION.SDK_INT +
                "&applicationId=" + DeviceInfoHelper.getApplicationId(context) +
                "&appVersionCode=" + DeviceInfoHelper.getAppVersionCode(context);
        Log.i(getClass().getName(), "fileUrl: " + fileUrl);

        String fileName = applicationVersionGson.getApplication().getPackageName() + "-" + applicationVersionGson.getVersionCode() + ".apk";
        Log.i(getClass().getName(), "fileName: " + fileName);

        Log.i(getClass().getName(), "Downloading APK: " + applicationVersionGson.getApplication().getPackageName() + " (version " + applicationVersionGson.getVersionCode() + ", " + applicationVersionGson.getFileSizeInKb() + "kB)");
        File apkFile = ApkLoader.loadApk(fileUrl, fileName, context);
        Log.i(getClass().getName(), "apkFile: " + apkFile);
        if ((apkFile == null) || !apkFile.exists()) {
            Log.i(getClass().getName(), "APK download failed: " + fileUrl);
        } else {
            Log.i(getClass().getName(), "Installing APK: " + applicationVersionGson.getApplication().getPackageName() + " (version " + applicationVersionGson.getVersionCode() + ")");
            String command = "pm install -r -g " + apkFile.getAbsolutePath(); // https://developer.android.com/studio/command-line/shell.html#pm
            if ("KFFOWI".equals(DeviceInfoHelper.getDeviceModel(context))) {
                // The '-g' command does not work on Amazon Fire: "Error: Unknown option: -g"
                command = "pm install -r " + apkFile.getAbsolutePath();
            }
            Log.i(getClass().getName(), "command: " + command);
            try {
                Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
                process.waitFor();

                InputStream inputStreamSuccess = process.getInputStream();
                if (inputStreamSuccess != null) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStreamSuccess));
                    String successMessage = bufferedReader.readLine();
                    Log.i(getClass().getName(), "successMessage: " + successMessage);
                    if (!"Success".equals(successMessage)) {
                        Log.i(getClass().getName(), "APK installation failed: " + applicationVersionGson.getApplication().getPackageName() + " (version " + applicationVersionGson.getVersionCode() + ")");
                    }

                    String startCommand = applicationVersionGson.getStartCommand();
                    if (!TextUtils.isEmpty(startCommand)) {
                        // Expected format: "adb shell <startCommand>"
                        Log.i(getClass().getName(), "startCommand: " + startCommand);

                        process = Runtime.getRuntime().exec(new String[]{"su", "-c", startCommand});
                        process.waitFor();

                        inputStreamSuccess = process.getInputStream();
                        if (inputStreamSuccess != null) {
                            bufferedReader = new BufferedReader(new InputStreamReader(inputStreamSuccess));
                            successMessage = bufferedReader.readLine();
                            Log.i(getClass().getName(), "startCommand successMessage: " + successMessage);
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

    private void uninstallApk(ApplicationGson applicationGson) {
        Log.i(getClass().getName(), "uninstallApk");

        Log.i(getClass().getName(), "Uninstalling APK: " + applicationGson.getPackageName());
        String command = "pm uninstall " + applicationGson.getPackageName();
        Log.i(getClass().getName(), "command: " + command);
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            process.waitFor();

            InputStream inputStreamSuccess = process.getInputStream();
            if (inputStreamSuccess != null) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStreamSuccess));
                String successMessage = bufferedReader.readLine();
                Log.i(getClass().getName(), "successMessage: " + successMessage);
                if (!"Success".equals(successMessage)) {
                    Log.e(getClass().getName(), "APK uninstallation failed: " + applicationGson.getPackageName());
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

    @Override
    protected void onPostExecute(Void v) {
        Log.i(getClass().getName(), "onPostExecute");
        super.onPostExecute(v);
    }
}
