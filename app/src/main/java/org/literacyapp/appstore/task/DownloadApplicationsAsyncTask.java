package org.literacyapp.appstore.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.log4j.Logger;
import org.literacyapp.appstore.util.DeviceInfoHelper;
import org.literacyapp.appstore.util.EnvironmentSettings;
import org.literacyapp.appstore.util.JsonLoader;
import org.literacyapp.model.enums.Locale;
import org.literacyapp.model.json.admin.application.ApplicationJson;

import java.lang.reflect.Type;
import java.util.List;

public class DownloadApplicationsAsyncTask extends AsyncTask<Object, Void, List<ApplicationJson>> {

    private Logger logger = Logger.getLogger(getClass());

    private Context context;

    public DownloadApplicationsAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected List<ApplicationJson> doInBackground(Object... objects) {
        logger.info("doInBackground");

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
        for (ApplicationJson applicationJson : applicationJsonList) {
            // Check if the application is already installed
            // TODO

            // If the application is not installed, download the APK file and install it
            // TODO

            // If already installed, check if a newer version is available for download
            // TODO

            // If a newer version is available, download the APK file and install it
            // TODO
        }

        // Update PREF_LAST_SYNCHRONIZATION

        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        logger.info("onProgressUpdate");
        super.onProgressUpdate(values);


    }

    @Override
    protected void onPostExecute(List<ApplicationJson> applicationJsonList) {
        logger.info("onPostExecute");
        super.onPostExecute(applicationJsonList);


    }
}
