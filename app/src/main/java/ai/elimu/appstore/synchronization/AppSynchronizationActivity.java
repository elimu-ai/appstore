package ai.elimu.appstore.synchronization;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ai.elimu.appstore.BaseApplication;
import ai.elimu.appstore.R;
import ai.elimu.appstore.dao.ApplicationDao;
import ai.elimu.appstore.dao.ApplicationVersionDao;
import ai.elimu.appstore.model.Application;
import ai.elimu.appstore.model.ApplicationVersion;
import ai.elimu.appstore.onboarding.LicenseNumberActivity;
import ai.elimu.appstore.service.AppCollectionService;
import ai.elimu.appstore.service.ApplicationService;
import ai.elimu.appstore.util.ChecksumHelper;
import ai.elimu.appstore.util.ConnectivityHelper;
import ai.elimu.appstore.util.DeviceInfoHelper;
import ai.elimu.appstore.util.UserPrefsHelper;
import ai.elimu.model.enums.admin.ApplicationStatus;
import ai.elimu.model.gson.admin.ApplicationGson;
import ai.elimu.model.gson.admin.ApplicationVersionGson;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class AppSynchronizationActivity extends AppCompatActivity {

    private ApplicationService mApplicationService;
    private AppCollectionService mAppCollectionService;
    private View appSyncLoadingContainer;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static final String PREF_LAST_SYNCHRONIZATION = "pref_last_synchronization";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i("onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_app_synchronization);

        appSyncLoadingContainer = findViewById(R.id.appSyncLoadingContainer);

        BaseApplication baseApplication = (BaseApplication) getApplication();
        mApplicationService = baseApplication.getRetrofit().create(ApplicationService.class);
        mAppCollectionService = baseApplication.getRetrofit()
                .create(AppCollectionService.class);
    }

    @Override
    protected void onStart() {
        Timber.i("onStart");
        super.onStart();

        boolean isWifiEnabled = ConnectivityHelper.isWifiEnabled(this);
        Timber.i("isWifiEnabled: " + isWifiEnabled);
        boolean isWifiConnected = ConnectivityHelper.isWifiConnected(this);
        Timber.i("isWifiConnected: " + isWifiConnected);

        /**
         * Check if server is reachable to start network API call
         */
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                boolean isServerReachable = ConnectivityHelper.isServerReachable(AppSynchronizationActivity.this);
                Timber.i("isServerReachable: " + isServerReachable);

                if (!isServerReachable) {
                    Timber.w(getString(R.string.server_is_not_reachable));
                } else {

                    /**
                     * Start download applications info
                     */
                    appSyncLoadingContainer.setVisibility(View.VISIBLE);

                    // If AppCollection from custom Project, use a different URL (see LicenseNumberActivity)
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    Long appCollectionId = sharedPreferences.getLong(LicenseNumberActivity.PREF_APP_COLLECTION_ID, 0);
                    Timber.i("appCollectionId: " + appCollectionId);
                    Call<ResponseBody> call;
                    if (appCollectionId > 0) {
                        // See https://github.com/elimu-ai/webapp/blob/master/REST_API_REFERENCE.md#read-applications
                        /**
                         * Download apps using app collection id
                         */
                        call = mAppCollectionService.getApplicationListByCollectionId(
                                appCollectionId,
                                sharedPreferences.getString(LicenseNumberActivity.PREF_LICENSE_EMAIL, null),
                                sharedPreferences.getString(LicenseNumberActivity.PREF_LICENSE_NUMBER, null)
                        );

                    } else {

                        /**
                         * Download apps using device info
                         */
                        call = mApplicationService.getApplicationList(
                                DeviceInfoHelper.getDeviceId(getApplicationContext()),
                                ChecksumHelper.getChecksum(getApplicationContext()),
                                UserPrefsHelper.getLocale(getApplicationContext()).toString(),
                                DeviceInfoHelper.getDeviceModel(getApplicationContext()),
                                Build.VERSION.SDK_INT,
                                DeviceInfoHelper.getApplicationId(getApplicationContext()),
                                DeviceInfoHelper.getAppVersionCode(getApplicationContext()));

                    }

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            appSyncLoadingContainer.setVisibility(View.GONE);
                            processAppListData(response);
                            displayAppList();

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            appSyncLoadingContainer.setVisibility(View.GONE);
                            t.printStackTrace();
                        }
                    });

                }
            }
        });

    }

    /**
     * Handle app list response data gotten from API by storing data to SQLite database
     * @param response The API response
     */
    private void processAppListData(@NonNull Response<ResponseBody> response) {

        ApplicationDao applicationDao = ((BaseApplication) getApplicationContext()).getDaoSession().getApplicationDao();
        ApplicationVersionDao applicationVersionDao = ((BaseApplication) getApplicationContext()).getDaoSession().getApplicationVersionDao();

        // If AppCollection from custom Project, use a different URL (see LicenseNumberActivity)
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Long appCollectionId = sharedPreferences.getLong(LicenseNumberActivity.PREF_APP_COLLECTION_ID, 0);
        Timber.i("appCollectionId: " + appCollectionId);

        try {
            String jsonResponse = response.body().string();
            Timber.i("jsonResponse: " + jsonResponse);
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
                sharedPreferences.edit().putLong(PREF_LAST_SYNCHRONIZATION, Calendar.getInstance().getTimeInMillis()).commit();
            }
        } catch (JSONException e) {
            Log.e(getClass().getName(), null, e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Go to app list activity
     */
    private void displayAppList() {
        // Display list of apps
        Intent intent = new Intent(getApplicationContext(), AppListActivity.class);
        startActivity(intent);
        finish();
    }
}
