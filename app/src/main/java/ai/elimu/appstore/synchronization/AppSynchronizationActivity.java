package ai.elimu.appstore.synchronization;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.List;

import ai.elimu.appstore.BaseApplication;
import ai.elimu.appstore.BuildConfig;
import ai.elimu.appstore.R;
import ai.elimu.appstore.dao.ApplicationDao;
import ai.elimu.appstore.dao.ApplicationVersionDao;
import ai.elimu.appstore.model.Application;
import ai.elimu.appstore.model.ApplicationVersion;
import ai.elimu.appstore.util.AppPrefs;
import ai.elimu.appstore.util.ChecksumHelper;
import ai.elimu.appstore.util.ConnectivityHelper;
import ai.elimu.appstore.util.DeviceInfoHelper;
import ai.elimu.appstore.util.JsonLoader;
import ai.elimu.appstore.util.UserPrefsHelper;
import ai.elimu.model.enums.admin.ApplicationStatus;
import ai.elimu.model.gson.admin.ApplicationGson;
import ai.elimu.model.gson.admin.ApplicationVersionGson;
import timber.log.Timber;

public class AppSynchronizationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i("onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_app_synchronization);
    }

    @Override
    protected void onStart() {
        Timber.i("onStart");
        super.onStart();

        new DownloadAppListAsyncTask(getApplicationContext()).execute();
    }


    /**
     * Downloads a list of apps from the server and stores them in an SQLite database.
     */
    public class DownloadAppListAsyncTask extends AsyncTask<Void, Void, Void> {

        private Context context;

        private ApplicationDao applicationDao;

        private ApplicationVersionDao applicationVersionDao;

        public DownloadAppListAsyncTask(Context context) {
            this.context = context;

            BaseApplication baseApplication = (BaseApplication) context;
            applicationDao = baseApplication.getDaoSession().getApplicationDao();
            applicationVersionDao = baseApplication.getDaoSession().getApplicationVersionDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Timber.i("doInBackground");

            boolean isWifiEnabled = ConnectivityHelper.isWifiEnabled(context);
            Timber.i("isWifiEnabled: " + isWifiEnabled);
            boolean isWifiConnected = ConnectivityHelper.isWifiConnected(context);
            Timber.i("isWifiConnected: " + isWifiConnected);
            boolean isServerReachable = ConnectivityHelper.isServerReachable(context);
            Timber.i("isServerReachable: " + isServerReachable);
            /*if (!isWifiEnabled) {
                Timber.w(context.getString(R.string.wifi_needs_to_be_enabled));
            } else if (!isWifiConnected) {
                Timber.w(context.getString(R.string.wifi_needs_to_be_connected));
            } else */if (!isServerReachable) {
                Timber.w(context.getString(R.string.server_is_not_reachable));
            } else {
                // Download List of applications
                String url = BuildConfig.REST_URL + "/application/list" +
                        "?deviceId=" + DeviceInfoHelper.getDeviceId(context) +
                        "&checksum=" + ChecksumHelper.getChecksum(context) +
                        "&locale=" + UserPrefsHelper.getLocale(context) +
                        "&deviceModel=" + DeviceInfoHelper.getDeviceModel(context) +
                        "&osVersion=" + Build.VERSION.SDK_INT +
                        "&applicationId=" + DeviceInfoHelper.getApplicationId(context) +
                        "&appVersionCode=" + DeviceInfoHelper.getAppVersionCode(context);

                // If AppCollection from custom Project, use a different URL (see LicenseNumberActivity)
                Long appCollectionId = AppPrefs.getAppCollectionId();
                Timber.i("appCollectionId: " + appCollectionId);
                if (appCollectionId > 0) {
                    // See https://github.com/elimu-ai/webapp/blob/master/REST_API_REFERENCE.md#read-applications
                    url = BuildConfig.REST_URL + "/project/app-collections/" + appCollectionId + "/applications" +
                            "?licenseEmail=" + AppPrefs.getLicenseEmail() +
                            "&licenseNumber=" + AppPrefs.getLicenseNumber();
                }

                String jsonResponse = JsonLoader.loadJson(url);
                Timber.i("jsonResponse: " + jsonResponse);
                try {
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    if (!"success".equals(jsonObject.getString("result"))) {
                        Timber.w("Download failed");
                        String errorDescription = jsonObject.getString("description");
                        Timber.w("errorDescription: " + errorDescription);
                    } else {
                        JSONArray jsonArrayApplications = jsonObject.getJSONArray("applications");
                        for (int i = 0; i < jsonArrayApplications.length(); i++) {
                            Type type = new TypeToken<ApplicationGson>(){}.getType();
                            ApplicationGson applicationGson = new Gson().fromJson(jsonArrayApplications.getString(i), type);
                            Timber.i("Synchronizing APK " + (i + 1) + "/" + jsonArrayApplications.length() + ": " + applicationGson.getPackageName() + " (status " + applicationGson.getApplicationStatus() + ")");

                            Application application = applicationDao.load(applicationGson.getId());
                            if (application == null) {
                                // Store new Application in database
                                application = new Application();
                                application.setId(applicationGson.getId());
                                application.setLocale(applicationGson.getLocale());
                                application.setPackageName(applicationGson.getPackageName());
                                application.setLiteracySkills(applicationGson.getLiteracySkills());
                                application.setNumeracySkills(applicationGson.getNumeracySkills());
                                application.setApplicationStatus(applicationGson.getApplicationStatus());
                                long id = applicationDao.insert(application);
                                Timber.i("Stored Application in database with id " + id);

                                if (application.getApplicationStatus() == ApplicationStatus.ACTIVE) {
                                    // Store ApplicationVersions
                                    List<ApplicationVersionGson> applicationVersionGsons = applicationGson.getApplicationVersions();
                                    Timber.i("applicationVersionGsons.size(): " + applicationVersionGsons.size());
                                    for (ApplicationVersionGson applicationVersionGson : applicationVersionGsons) {
                                        ApplicationVersion applicationVersion = applicationVersionDao.load(applicationVersionGson.getId());
                                        if (applicationVersion == null) {
                                            // Store new ApplicationVersion in database
                                            applicationVersion = new ApplicationVersion();
                                            applicationVersion.setId(applicationVersionGson.getId());
                                            applicationVersion.setApplication(application);
                                            applicationVersion.setFileSizeInKb(applicationVersionGson.getFileSizeInKb());
                                            applicationVersion.setFileUrl(applicationVersionGson.getFileUrl());
                                            applicationVersion.setContentType(applicationVersionGson.getContentType());
                                            applicationVersion.setVersionCode(applicationVersionGson.getVersionCode());
                                            applicationVersion.setStartCommand(applicationVersionGson.getStartCommand());
                                            applicationVersion.setTimeUploaded(applicationVersionGson.getTimeUploaded());
                                            long applicationVersionId = applicationVersionDao.insert(applicationVersion);
                                            Timber.i("Stored ApplicationVersion in database with id " + applicationVersionId);
                                        }
                                    }
                                }
                            } else {
                                // Update existing Application in database
                                application.setId(applicationGson.getId());
                                application.setLocale(applicationGson.getLocale());
                                application.setPackageName(applicationGson.getPackageName());
                                application.setLiteracySkills(applicationGson.getLiteracySkills());
                                application.setNumeracySkills(applicationGson.getNumeracySkills());
                                application.setApplicationStatus(applicationGson.getApplicationStatus());
                                applicationDao.update(application);
                                Timber.i("Updated Application in database with id " + application.getId());

                                if (application.getApplicationStatus() == ApplicationStatus.ACTIVE) {
                                    // Update ApplicationVersions
                                    List<ApplicationVersionGson> applicationVersionGsons = applicationGson.getApplicationVersions();
                                    Timber.i("applicationVersionGsons.size(): " + applicationVersionGsons.size());
                                    for (ApplicationVersionGson applicationVersionGson : applicationVersionGsons) {
                                        ApplicationVersion applicationVersion = applicationVersionDao.load(applicationVersionGson.getId());

                                        if (applicationVersion == null) {
                                            // Store new ApplicationVersion in database
                                            applicationVersion = new ApplicationVersion();
                                            applicationVersion.setId(applicationVersionGson.getId());
                                            applicationVersion.setApplication(application);
                                            applicationVersion.setFileSizeInKb(applicationVersionGson.getFileSizeInKb());
                                            applicationVersion.setFileUrl(applicationVersionGson.getFileUrl());
                                            applicationVersion.setContentType(applicationVersionGson.getContentType());
                                            applicationVersion.setVersionCode(applicationVersionGson.getVersionCode());
                                            applicationVersion.setStartCommand(applicationVersionGson.getStartCommand());
                                            applicationVersion.setTimeUploaded(applicationVersionGson.getTimeUploaded());
                                            long applicationVersionId = applicationVersionDao.insert(applicationVersion);
                                            Timber.i("Stored ApplicationVersion in database with id " + applicationVersionId);
                                        }
                                    }
                                }
                            }
                        }
                        Timber.i("Synchronization complete!");

                        // Update time of last synchronization
                        AppPrefs.saveLastSyncTime(Calendar.getInstance().getTimeInMillis());
                    }
                } catch (JSONException e) {
                    Log.e(getClass().getName(), null, e);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            Timber.i("onPostExecute");
            super.onPostExecute(v);

            // Display list of apps
            Intent intent = new Intent(getApplicationContext(), AppListActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
