package ai.elimu.appstore.asynctask;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ai.elimu.appstore.BaseApplication;
import ai.elimu.appstore.BuildConfig;
import ai.elimu.appstore.R;
import ai.elimu.appstore.dao.ApplicationDao;
import ai.elimu.appstore.model.Application;
import ai.elimu.appstore.util.ApkLoader;
import ai.elimu.appstore.util.ChecksumHelper;
import ai.elimu.appstore.util.ConnectivityHelper;
import ai.elimu.appstore.util.DeviceInfoHelper;
import ai.elimu.appstore.util.JsonLoader;
import ai.elimu.appstore.util.UserPrefsHelper;
import ai.elimu.model.enums.admin.ApplicationStatus;
import ai.elimu.model.gson.admin.ApplicationGson;
import ai.elimu.model.gson.admin.ApplicationVersionGson;
import timber.log.Timber;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Calendar;

@Deprecated
public class DownloadApplicationsAsyncTask extends AsyncTask<Object, String, Void> {

    public static final String PREF_LAST_SYNCHRONIZATION = "pref_last_synchronization";

    private Context context;

    private ApplicationDao applicationDao;

    public DownloadApplicationsAsyncTask(Context context) {
        this.context = context;

        BaseApplication baseApplication = (BaseApplication) context;
        applicationDao = baseApplication.getDaoSession().getApplicationDao();
    }

    @Override
    protected Void doInBackground(Object... objects) {
        Timber.i("doInBackground");

//        boolean isWifiEnabled = ConnectivityHelper.isWifiEnabled(context);
//        Timber.i("isWifiEnabled: " + isWifiEnabled);
//        boolean isWifiConnected = ConnectivityHelper.isWifiConnected(context);
//        Timber.i("isWifiConnected: " + isWifiConnected);
//        boolean isServerReachable = ConnectivityHelper.isServerReachable(context);
//        Timber.i("isServerReachable: " + isServerReachable);
//        if (!isWifiEnabled) {
//            Timber.w(context.getString(R.string.wifi_needs_to_be_enabled));
//        } else if (!isWifiConnected) {
//            Timber.w(context.getString(R.string.wifi_needs_to_be_connected));
//        } else if (!isServerReachable) {
//            Timber.w(context.getString(R.string.server_is_not_reachable));
//        } else {
//            // Download List of applications
//            String url = BuildConfig.REST_URL + "/admin/application/list" +
//                    "?deviceId=" + DeviceInfoHelper.getDeviceId(context) +
//                    "&checksum=" + ChecksumHelper.getChecksum(context) +
//                    "&locale=" + UserPrefsHelper.getLocale(context) +
//                    "&deviceModel=" + DeviceInfoHelper.getDeviceModel(context) +
//                    "&osVersion=" + Build.VERSION.SDK_INT +
//                    "&applicationId=" + DeviceInfoHelper.getApplicationId(context) +
//                    "&appVersionCode=" + DeviceInfoHelper.getAppVersionCode(context);
//            String jsonResponse = JsonLoader.loadJson(url);
//            Timber.i("jsonResponse: " + jsonResponse);
//            try {
//                JSONObject jsonObject = new JSONObject(jsonResponse);
//                if (!"success".equals(jsonObject.getString("result"))) {
//                    Timber.w("Download failed");
//                    String errorDescription = jsonObject.getString("description");
//                } else {
//                    JSONArray jsonArrayApplications = jsonObject.getJSONArray("applications");
//                    for (int i = 0; i < jsonArrayApplications.length(); i++) {
//                        Type type = new TypeToken<ApplicationGson>() {}.getType();
//                        ApplicationGson applicationGson = new Gson().fromJson(jsonArrayApplications.getString(i), type);
//                        Timber.i("Synchronizing APK " + (i + 1) + "/" + jsonArrayApplications.length() + ": " + applicationGson.getPackageName() + " (status " + applicationGson.getApplicationStatus() + ")");
//
//                        Application application = applicationDao.load(applicationGson.getId());
//                        if (application == null) {
//                            // Store new Application in database
//                            application = new Application();
//                            application.setId(applicationGson.getId());
//                            application.setLocale(applicationGson.getLocale());
//                            application.setPackageName(applicationGson.getPackageName());
//                            application.setLiteracySkills(applicationGson.getLiteracySkills());
//                            application.setNumeracySkills(applicationGson.getNumeracySkills());
//                            application.setApplicationStatus(applicationGson.getApplicationStatus());
//                            if (applicationGson.getApplicationStatus() == ApplicationStatus.ACTIVE) {
//                                ApplicationVersionGson applicationVersionGson = applicationGson.getApplicationVersions().get(0);
//                                application.setVersionCode(applicationVersionGson.getVersionCode());
//                                application.setStartCommand(applicationVersionGson.getStartCommand());
//                            }
//                            long id = applicationDao.insert(application);
//                            Timber.i("Stored Application in database with id " + id);
//                        } else {
//                            // Update existing Application in database
//                            application.setId(applicationGson.getId());
//                            application.setLocale(applicationGson.getLocale());
//                            application.setPackageName(applicationGson.getPackageName());
//                            application.setLiteracySkills(applicationGson.getLiteracySkills());
//                            application.setNumeracySkills(applicationGson.getNumeracySkills());
//                            application.setApplicationStatus(applicationGson.getApplicationStatus());
//                            if (applicationGson.getApplicationStatus() == ApplicationStatus.ACTIVE) {
//                                ApplicationVersionGson applicationVersionGson = applicationGson.getApplicationVersions().get(0);
//                                application.setVersionCode(applicationVersionGson.getVersionCode());
//                                application.setStartCommand(applicationVersionGson.getStartCommand());
//                            }
//                            applicationDao.update(application);
//                            Timber.i("Updated Application in database with id " + application.getId());
//                        }
//
//                        // Download APK if missing from SD card
//                        if (applicationGson.getApplicationStatus() == ApplicationStatus.ACTIVE) {
//                            String language = UserPrefsHelper.getLocale(context).getLanguage();
//                            File apkDirectory = new File(Environment.getExternalStorageDirectory() + "/.elimu-ai/appstore/apks/" + language);
//                            ApplicationVersionGson applicationVersionGson = applicationGson.getApplicationVersions().get(0);
//                            String fileName = applicationVersionGson.getApplication().getPackageName() + "-" + applicationVersionGson.getVersionCode() + ".apk";
//                            File apkFile = new File(apkDirectory, fileName);
//                            Timber.i("apkFile: " + apkFile);
//                            Timber.i("apkFile.exists(): " + apkFile.exists());
//                            if (!apkFile.exists()) {
//                                Timber.i("APK file (" + fileName + ") missing from SD card. Downloading...");
//                                downloadApk(applicationVersionGson);
//                            }
//                        }
//
//                        // Delete/update/install application
//                        PackageManager packageManager = context.getPackageManager();
//                        try {
//                            PackageInfo packageInfo = packageManager.getPackageInfo(applicationGson.getPackageName(), PackageManager.GET_ACTIVITIES);
//                            Timber.i("The application is already installed: " + applicationGson.getPackageName());
//
//                            // Check if the Application has been deleted/deactivated on the website
//                            if ((applicationGson.getApplicationStatus() == ApplicationStatus.DELETED)
//                                    || (applicationGson.getApplicationStatus() == ApplicationStatus.INACTIVE)) {
//                                // Delete application
//                                uninstallApk(applicationGson);
//                            } else if (applicationGson.getApplicationStatus() == ApplicationStatus.ACTIVE) {
//                                // Check if a newer version is available
//                                Timber.i("packageInfo.versionCode: " + packageInfo.versionCode);
//                                ApplicationVersionGson applicationVersionGson = applicationGson.getApplicationVersions().get(0);
//                                Timber.i("Newest version available: " + applicationVersionGson.getVersionCode());
//                                if (packageInfo.versionCode < applicationVersionGson.getVersionCode()) {
//                                    // Download the APK and install it
//                                    installApk(applicationVersionGson);
//                                }
//                            }
//                        } catch (PackageManager.NameNotFoundException e) {
//                            Timber.i("The application is not installed: " + applicationGson.getPackageName());
//
//                            if (applicationGson.getApplicationStatus() == ApplicationStatus.ACTIVE) {
//                                // Install the APK
//                                ApplicationVersionGson applicationVersionGson = applicationGson.getApplicationVersions().get(0);
//                                installApk(applicationVersionGson);
//                            }
//                        }
//                    }
//                    Timber.i("Synchronization complete!");
//
//                    // Update time of last synchronization
//                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//                    sharedPreferences.edit().putLong(PREF_LAST_SYNCHRONIZATION, Calendar.getInstance().getTimeInMillis()).commit();
//                }
//            } catch (JSONException e) {
//                Log.e(getClass().getName(), null, e);
//            }
//        }

        return null;
    }

    private File downloadApk(ApplicationVersionGson applicationVersionGson) {
        Timber.i("downloadApk");

        String fileUrl = BuildConfig.BASE_URL + applicationVersionGson.getFileUrl() +
                "?deviceId=" + DeviceInfoHelper.getDeviceId(context) +
                "&checksum=" + ChecksumHelper.getChecksum(context) +
                "&locale=" + UserPrefsHelper.getLocale(context) +
                "&deviceModel=" + DeviceInfoHelper.getDeviceModel(context) +
                "&osVersion=" + Build.VERSION.SDK_INT +
                "&applicationId=" + applicationVersionGson.getApplication().getId() +
                "&appVersionCode=" + DeviceInfoHelper.getAppVersionCode(context);
        Timber.i("fileUrl: " + fileUrl);

        String fileName = applicationVersionGson.getApplication().getPackageName() + "-" + applicationVersionGson.getVersionCode() + ".apk";
        Timber.i("fileName: " + fileName);

        Timber.i("Downloading APK: " + applicationVersionGson.getApplication().getPackageName() + " (version " + applicationVersionGson.getVersionCode() + ", " + applicationVersionGson.getFileSizeInKb() + "kB)");
        File apkFile = ApkLoader.loadApk(fileUrl, fileName, context);
        Timber.i("apkFile: " + apkFile);
        if ((apkFile == null) || !apkFile.exists()) {
            Timber.w("APK download failed: " + fileUrl);
        } else {
            Timber.i("APK downloaded: " + applicationVersionGson.getApplication().getPackageName() + " (version " + applicationVersionGson.getVersionCode() + ")");
        }

        return apkFile;
    }

    private void installApk(ApplicationVersionGson applicationVersionGson) {
        Timber.i("installApk");

        String fileName = applicationVersionGson.getApplication().getPackageName() + "-" + applicationVersionGson.getVersionCode() + ".apk";
        Timber.i("fileName: " + fileName);

        String language = UserPrefsHelper.getLocale(context).getLanguage();
        File apkDirectory = new File(Environment.getExternalStorageDirectory() + "/.elimu-ai/appstore/apks/" + language);
        Timber.i("apkDirectory: " + apkDirectory);
        if (!apkDirectory.exists()) {
            apkDirectory.mkdirs();
        }

        File apkFile = new File(apkDirectory, fileName);
        Timber.i("apkFile: " + apkFile);
        Timber.i("apkFile.exists(): " + apkFile.exists());

        if ((apkFile == null) || !apkFile.exists()) {
            Timber.w("APK installation failed: " + apkFile);
        } else {
            Timber.i("Installing APK: " + applicationVersionGson.getApplication().getPackageName() + " (version " + applicationVersionGson.getVersionCode() + ")");
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
                        Timber.i("APK installation failed: " + applicationVersionGson.getApplication().getPackageName() + " (version " + applicationVersionGson.getVersionCode() + ")");
                    }

                    String startCommand = applicationVersionGson.getStartCommand();
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
                            Timber.w("startCommand errorMessage: " + errorMessage);
                        }
                    }
                }

                InputStream inputStreamError = process.getErrorStream();
                if (inputStreamError != null) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStreamError));
                    String errorMessage = bufferedReader.readLine();
                    Timber.w("errorMessage: " + errorMessage);
                }
            } catch (IOException e) {
                Timber.e("IOException: " + command, e);
            } catch (InterruptedException e) {
                Timber.e("InterruptedException: " + command, e);
            }
        }
    }

    private void uninstallApk(ApplicationGson applicationGson) {
        Timber.i("uninstallApk");

        Timber.i("Uninstalling APK: " + applicationGson.getPackageName());
        String command = "pm uninstall " + applicationGson.getPackageName();
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
        Timber.i("onPostExecute");
        super.onPostExecute(v);
    }
}
