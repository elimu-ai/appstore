package ai.elimu.appstore.synchronization;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.util.Preconditions;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ai.elimu.appstore.BuildConfig;
import ai.elimu.appstore.R;
import ai.elimu.appstore.model.ApplicationVersion;
import ai.elimu.appstore.util.ChecksumHelper;
import ai.elimu.appstore.util.DeviceInfoHelper;
import ai.elimu.appstore.util.UserPrefsHelper;
import timber.log.Timber;

/**
 * Downloads APK file and updates progress bar in the UI during the download.
 */
public class DownloadApplicationAsyncTask extends AsyncTask<ApplicationVersion, Integer,
        Integer> {

    private ApplicationVersion applicationVersion;

    private ProgressBar progressBarDownloadProgress;

    private TextView textViewDownloadProgress;

    private Button btnInstall;

    private Button btnDownload;

    private Context context;

    private DownloadCompleteCallback downloadCompleteCallback;

    private ProgressUpdateCallback progressUpdateCallback;

    public DownloadApplicationAsyncTask(@NonNull Context context,
                                        ProgressBar progressBarDownloadProgress,
                                        TextView textViewDownloadProgress,
                                        Button buttonInstall,
                                        Button buttonDownload,
                                        @NonNull DownloadCompleteCallback downloadCompleteCallback,
                                        @NonNull ProgressUpdateCallback progressUpdateCallback) {
        this.context = Preconditions.checkNotNull(context);
        this.progressBarDownloadProgress = progressBarDownloadProgress;
        this.textViewDownloadProgress = textViewDownloadProgress;
        this.btnInstall = buttonInstall;
        this.btnDownload = buttonDownload;
        this.downloadCompleteCallback = Preconditions.checkNotNull(downloadCompleteCallback);
        this.progressUpdateCallback = Preconditions.checkNotNull(progressUpdateCallback);
    }

    @Override
    protected Integer doInBackground(ApplicationVersion... applicationVersions) {
        Timber.i("doInBackground");

        applicationVersion = applicationVersions[0];
        Timber.i("applicationVersion.getApplication(): " + applicationVersion.getApplication());
        Timber.i("applicationVersion.getFileSizeInKb(): " + applicationVersion
                .getFileSizeInKb());
        Timber.i("applicationVersion.getFileUrl(): " + applicationVersion.getFileUrl());
        Timber.i("applicationVersion.getContentType(): " + applicationVersion.getContentType());
        Timber.i("applicationVersion.getVersionCode(): " + applicationVersion.getVersionCode());
        Timber.i("applicationVersion.getStartCommand(): " + applicationVersion
                .getStartCommand());
        Timber.i("applicationVersion.getTimeUploaded().getTime(): " + applicationVersion
                .getTimeUploaded().getTime());

        // Reset to initial state
        Integer fileSizeInKbsDownloaded = 0;
        publishProgress(fileSizeInKbsDownloaded);

        // Download APK file and store it on SD card
        String fileUrl = BuildConfig.BASE_URL + applicationVersion.getFileUrl() +
                "?deviceId=" + DeviceInfoHelper.getDeviceId(context) +
                "&checksum=" + ChecksumHelper.getChecksum(context) +
                "&locale=" + UserPrefsHelper.getLocale(context) +
                "&deviceModel=" + DeviceInfoHelper.getDeviceModel(context) +
                "&osVersion=" + Build.VERSION.SDK_INT +
                "&applicationId=" + applicationVersion.getApplication().getId() +
                "&appVersionCode=" + DeviceInfoHelper.getAppVersionCode(context);
        if (!TextUtils.isEmpty(UserPrefsHelper.getLicenseEmail(context))) {
            // Custom Project
            fileUrl += "&licenseEmail=" + UserPrefsHelper.getLicenseEmail(context);
            fileUrl += "&licenseNumber=" + UserPrefsHelper.getLicenseNumber(context);
        }
        Timber.i("fileUrl: " + fileUrl);

        String fileName = applicationVersion.getApplication().getPackageName() + "-" +
                applicationVersion.getVersionCode() + ".apk";
        Timber.i("fileName: " + fileName);

        Timber.i("Downloading APK: " + applicationVersion.getApplication().getPackageName() +
                " (version " + applicationVersion.getVersionCode() + ", " +
                applicationVersion.getFileSizeInKb() + "kB)");

//            File apkFile = ApkLoader.loadApk(fileUrl, fileName, context);
        // Copied from ApkLoader#loadApk:
        String urlValue = fileUrl;
        Timber.i("Downloading from " + urlValue + "...");

        String language = UserPrefsHelper.getLocale(context).getLanguage();
        File apkDirectory = new File(Environment.getExternalStorageDirectory() + "/" +
                ".elimu-ai/appstore/apks/" + language);
        Timber.i("apkDirectory: " + apkDirectory);
        if (!apkDirectory.exists()) {
            apkDirectory.mkdirs();
        }

        File apkFile = new File(apkDirectory, fileName);
        Timber.i("apkFile: " + apkFile);
        Timber.i("apkFile.exists(): " + apkFile.exists());

        if (!apkFile.exists()) {
            FileOutputStream fileOutputStream = null;
            String downloadedApkChecksum = "";
            try {
                URL url = new URL(urlValue);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                int responseCode = httpURLConnection.getResponseCode();
                Timber.i("responseCode: " + responseCode);
                InputStream inputStream = null;
                if (responseCode == 200) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader
                            (inputStream));
                    String errorResponse = "";
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        errorResponse += line;
                    }
                    Timber.w("errorResponse: " + errorResponse);
                    return null;
                }

//                    byte[] bytes = IOUtils.toByteArray(inputStream);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int bytesRead = 0;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);

                    fileSizeInKbsDownloaded += (bytesRead / 1024);

                    //Only update APK size every 100KB downloaded
                    if (fileSizeInKbsDownloaded % 100 == 0) {
                        publishProgress(fileSizeInKbsDownloaded);
                    }
                }
                byte[] bytes = byteArrayOutputStream.toByteArray();

                fileOutputStream = new FileOutputStream(apkFile);
                fileOutputStream.write(bytes);
                fileOutputStream.flush();

                downloadedApkChecksum = ChecksumHelper.calculateMd5(apkFile);
            } catch (MalformedURLException e) {
                Timber.e(e, "MalformedURLException");
            } catch (IOException e) {
                Timber.e(e, "IOException");
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        Timber.i(e, "IOException");
                    }
                }

                /**
                 * Delete downloaded APK file in case its checksum is invalid
                 */
                if (!downloadedApkChecksum.equals(applicationVersion.getChecksumMd5())) {
                    Timber.w("Invalid checksum. Deleting downloaded APK file: " + apkFile);
                    apkFile.delete();
                }
            }
        }

        Timber.i("apkFile: " + apkFile);

        return fileSizeInKbsDownloaded;
    }

    @Override
    protected void onProgressUpdate(Integer... fileSizeInKbsDownloadeds) {
        Timber.d("onProgressUpdate");
        super.onProgressUpdate(fileSizeInKbsDownloadeds);

        int fileSizeInKbsDownloaded = fileSizeInKbsDownloadeds[0];
        Timber.d("fileSizeInKbsDownloaded: " + fileSizeInKbsDownloaded);

        int progress = (fileSizeInKbsDownloaded * 100) / applicationVersion.getFileSizeInKb();
        Timber.d("progress: " + progress);

        // E.g. "6.00 MB/12.00 MB   50%"
        String progressText = String.format(context.getString(R.string
                        .app_list_download_progress_number), fileSizeInKbsDownloaded / 1024f,
                applicationVersion.getFileSizeInKb() / 1024f, progress);

        Timber.d("progressText: " + progressText);
        progressUpdateCallback.onProgressUpdated(progressText, progress);
    }

    @Override
    protected void onPostExecute(Integer fileSizeInKbsDownloaded) {
        Timber.i("onPostExecute");
        super.onPostExecute(fileSizeInKbsDownloaded);

        // Hide progress indicators
        progressBarDownloadProgress.setVisibility(View.GONE);
        textViewDownloadProgress.setVisibility(View.GONE);

        if ((fileSizeInKbsDownloaded == null) || (fileSizeInKbsDownloaded == 0)) {
            btnDownload.setVisibility(View.VISIBLE);
            btnInstall.setVisibility(View.GONE);
            Toast.makeText(context,
                    context.getString(R.string.app_list_check_internet_connection),
                    Toast.LENGTH_SHORT).show();
        } else {
            btnDownload.setVisibility(View.GONE);
            btnInstall.setVisibility(View.VISIBLE);
            downloadCompleteCallback.onDownloadCompleted();
        }

        Timber.i("fileSizeInKbsDownloaded: " + fileSizeInKbsDownloaded);
    }

    interface DownloadCompleteCallback {

        void onDownloadCompleted();
    }

    interface ProgressUpdateCallback {

        void onProgressUpdated(String progressText, int progress);

    }
}
