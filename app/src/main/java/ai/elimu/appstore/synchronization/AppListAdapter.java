package ai.elimu.appstore.synchronization;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ai.elimu.appstore.BaseApplication;
import ai.elimu.appstore.BuildConfig;
import ai.elimu.appstore.R;
import ai.elimu.appstore.dao.ApplicationVersionDao;
import ai.elimu.appstore.model.AppDownloadStatus;
import ai.elimu.appstore.model.Application;
import ai.elimu.appstore.model.ApplicationVersion;
import ai.elimu.appstore.receiver.PackageUpdateReceiver;
import ai.elimu.appstore.service.DownloadApplicationService;
import ai.elimu.appstore.service.DownloadCompleteCallback;
import ai.elimu.appstore.service.ProgressUpdateCallback;
import ai.elimu.appstore.util.ChecksumHelper;
import ai.elimu.appstore.util.ConnectivityHelper;
import ai.elimu.appstore.util.DeviceInfoHelper;
import ai.elimu.appstore.util.FileUtils;
import ai.elimu.appstore.util.UserPrefsHelper;
import ai.elimu.model.enums.Locale;
import ai.elimu.model.enums.admin.ApplicationStatus;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {

    private List<Application> applications;

    private List<AppDownloadStatus> appDownloadStatus;

    private Context context;

    private ApplicationVersionDao applicationVersionDao;

    private PackageUpdateReceiver packageUpdateReceiver;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private DownloadApplicationService downloadApplicationService;

    private BaseApplication baseApplication;

    private ProgressUpdateCallback progressUpdateCallback;

    private Handler uiHandler;

    public AppListAdapter(List<Application> applications, PackageUpdateReceiver packageUpdateReceiver, BaseApplication baseApplication) {
        this.applications = applications;
        this.packageUpdateReceiver = packageUpdateReceiver;
        this.baseApplication = baseApplication;
        initAppDownloadStatus();
    }

    private void initAppDownloadStatus() {
        appDownloadStatus = new ArrayList<>(applications.size());
        for (int i = 0; i < applications.size(); i++) {
            appDownloadStatus.add(new AppDownloadStatus());
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Timber.i("onBindViewHolder");

        final Application application = applications.get(position);
        final AppDownloadStatus downloadStatus = appDownloadStatus.get(position);
        holder.textViewTitle.setText(application.getPackageName());
        holder.textDownloadProgress.setText(downloadStatus.getDownloadProgressText());
        holder.progressBarDownload.setProgress(downloadStatus.getDownloadProgress());

        if (downloadStatus.isDownloading()) {
            holder.progressBarDownload.setVisibility(View.VISIBLE);
            holder.textDownloadProgress.setVisibility(View.VISIBLE);
            holder.btnDownload.setVisibility(View.GONE);
        } else {
            holder.progressBarDownload.setVisibility(View.GONE);
            holder.textDownloadProgress.setVisibility(View.GONE);
            holder.progressBarDownload.setProgress(0);
            holder.textDownloadProgress.setText("");
            holder.btnDownload.setVisibility(View.VISIBLE);
        }

        if (application.getApplicationStatus() != ApplicationStatus.ACTIVE) {
            // Do not allow APK download
            holder.textViewVersion.setText("ApplicationStatus: " + application.getApplicationStatus());
            holder.btnDownload.setVisibility(View.VISIBLE);
            holder.btnDownload.setEnabled(false);
            holder.imageAppIcon.setImageDrawable(context.getDrawable(R.drawable.ic_launcher));
            holder.btnInstall.setVisibility(View.GONE);
            // TODO: hide applications that are not active?
        } else {
            holder.btnDownload.setEnabled(true);

            // Fetch the latest APK version
            List<ApplicationVersion> applicationVersions = applicationVersionDao.queryBuilder()
                    .where(ApplicationVersionDao.Properties.ApplicationId.eq(application.getId()))
                    .orderDesc(ApplicationVersionDao.Properties.VersionCode)
                    .list();
            final ApplicationVersion applicationVersion = applicationVersions.get(0);

            if (!TextUtils.isEmpty(applicationVersion.getLabel())) {
                // Display the label of the Application (instead of packageName)
                holder.textViewTitle.setText(applicationVersion.getLabel());
            }

            // Display the versionName of the Application
            if (!TextUtils.isEmpty(applicationVersion.getVersionName())) {
                holder.textViewVersion.setText((applicationVersion.getFileSizeInKb() / 1024) + " MB • " + applicationVersion.getVersionName());
            } else {
                // If versionName empty (not returned from server), fall back to versionCode
                holder.textViewVersion.setText((applicationVersion.getFileSizeInKb() / 1024) + " MB • " + applicationVersion.getVersionCode());
            }

            // Check if the APK file has already been downloaded to the SD card
            Locale locale = UserPrefsHelper.getLocale(context);
            if (locale == null) {
                // The user typed a License for a custom Project, which does not use a specific Locale.
                // Fall back to English
                locale = Locale.EN;
            }
            String language = locale.getLanguage();
            String fileName = applicationVersion.getApplication().getPackageName() + "-" + applicationVersion.getVersionCode() + ".apk";
            File apkDirectory = new File(Environment.getExternalStorageDirectory() + "/" + ".elimu-ai/appstore/apks/" + language);
            final File existingApkFile = new File(apkDirectory, fileName);
            Timber.i("existingApkFile: " + existingApkFile);
            Timber.i("existingApkFile.exists(): " + existingApkFile.exists());
            if (existingApkFile.exists()) {
                holder.btnDownload.setVisibility(View.GONE);
                holder.btnInstall.setVisibility(View.VISIBLE);
            } else {
                if (!downloadStatus.isDownloading()) {
                    holder.btnDownload.setVisibility(View.VISIBLE);
                }
                holder.btnInstall.setVisibility(View.GONE);
            }

            // Check if the APK file has already been installed
            final PackageManager packageManager = context.getPackageManager();
            boolean isAppInstalled = true;
            try {
                packageManager.getApplicationInfo(application.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                isAppInstalled = false;
            }
            Timber.i("isAppInstalled: " + isAppInstalled);

            if (isAppInstalled) {
                holder.btnInstall.setVisibility(View.GONE);

                // Check if update is available for download
                try {
                    PackageInfo packageInfo = packageManager.getPackageInfo(application.getPackageName(), 0);
                    int versionCodeInstalled = packageInfo.versionCode;
                    Timber.i("versionCodeInstalled: " + versionCodeInstalled);
                    if (applicationVersion.getVersionCode() > versionCodeInstalled) {
                        // Update is available for download/install

                        // Change the button text
                        if (!existingApkFile.exists()) {
                            holder.btnDownload.setText(R.string.download_update);

                            // If download update is ongoing, hide the download update button. Otherwise, show the download update button.
                            if (!downloadStatus.isDownloading()) {
                                holder.btnDownload.setVisibility(View.VISIBLE);
                            } else {
                                holder.btnDownload.setVisibility(View.GONE);
                            }

                        } else {
                            holder.btnInstall.setVisibility(View.VISIBLE);
                            holder.btnInstall.setText(R.string.install_update);
                        }
                    } else {
                        holder.btnDownload.setVisibility(View.GONE);
                        holder.btnInstall.setVisibility(View.GONE);
                    }

                    // Extract icon from installed application
                    ApplicationInfo applicationInfo = packageManager.getApplicationInfo(application.getPackageName(), PackageManager.GET_META_DATA);
                    Resources resources = packageManager.getResourcesForApplication(application.getPackageName());
                    Drawable appIcon = resources.getDrawableForDensity(applicationInfo.icon, resources.getDisplayMetrics().densityDpi, null);
                    holder.imageAppIcon.setImageDrawable(appIcon);
                } catch (PackageManager.NameNotFoundException e) {
                    Timber.e(e);
                }
            } else if (existingApkFile.exists()) {
                // Extract app icon from downloaded APK if found
                PackageInfo packageInfo = packageManager.getPackageArchiveInfo(existingApkFile.getAbsolutePath(), 0);
                packageInfo.applicationInfo.sourceDir = existingApkFile.getAbsolutePath();
                packageInfo.applicationInfo.publicSourceDir = existingApkFile.getAbsolutePath();
                Drawable appIcon = packageInfo.applicationInfo.loadIcon(packageManager);
                holder.imageAppIcon.setImageDrawable(appIcon);
            } else {
                holder.imageAppIcon.setImageDrawable(context.getDrawable(R.drawable.ic_launcher));
            }

            holder.btnDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Timber.i("buttonDownload onClick");

                    if (!ConnectivityHelper.isNetworkAvailable(context)) {
                        Toast.makeText(context, context.getString(R.string.app_list_check_internet_connection), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Timber.i("Downloading " + application.getPackageName() + " (version " + applicationVersion.getVersionCode() + ")...");

                    holder.btnDownload.setVisibility(View.GONE);
                    holder.progressBarDownload.setVisibility(View.VISIBLE);
                    holder.textDownloadProgress.setVisibility(View.VISIBLE);
                    downloadStatus.setDownloading(true);
                    appDownloadStatus.set(position, downloadStatus);

                    // Initiate download of the latest APK version
                    Timber.i("applicationVersion: " + applicationVersion);

                    // Listen to download completed event to update app icon
                    final DownloadCompleteCallback downloadCompleteCallback = new DownloadCompleteCallback() {
                        @Override
                        public void onDownloadCompleted(String tempApkDir, String apkName) {
                            Timber.i("onDownloadCompleted");

                            // Move downloaded file to correct folder
                            String language = UserPrefsHelper.getLocale(context).getLanguage();
                            File correctApkDirectory = new File(Environment.getExternalStorageDirectory() + "/" + ".elimu-ai/appstore/apks/" + language);
                            if(!correctApkDirectory.exists()){
                                correctApkDirectory.mkdirs();
                            }

                            File srcFile = new File(tempApkDir, apkName);
                            File dstFile = new File(correctApkDirectory, apkName);

                            try {
                                FileUtils.moveFile(srcFile, dstFile);

                                // Change visibility of download/install buttons upon moving completion
                                downloadStatus.setDownloading(false);
                                holder.btnDownload.setVisibility(View.GONE);
                                holder.btnInstall.setVisibility(View.VISIBLE);
                                appDownloadStatus.set(position, downloadStatus);

                                // Set app icon upon download completion
                                PackageInfo packageInfo = packageManager.getPackageArchiveInfo(existingApkFile.getAbsolutePath(), 0);
                                if (packageInfo != null) {
                                    packageInfo.applicationInfo.sourceDir = existingApkFile.getAbsolutePath();
                                    packageInfo.applicationInfo.publicSourceDir = existingApkFile.getAbsolutePath();
                                    Drawable appIcon = packageInfo.applicationInfo.loadIcon(packageManager);
                                    holder.imageAppIcon.setImageDrawable(appIcon);
                                }
                            } catch (IOException e){
                                Timber.e(e);
                            }
                        }

                        @Override
                        public void onDownloadFailed(Integer fileSizeInKbsDownloaded) {
                            Timber.i("onDownloadFailed");

                            downloadStatus.setDownloading(false);
                            holder.btnDownload.setVisibility(View.VISIBLE);
                            holder.btnInstall.setVisibility(View.GONE);
                            if (fileSizeInKbsDownloaded == 0 || fileSizeInKbsDownloaded == null) {
                                Toast.makeText(context, context.getString(R.string.app_list_check_internet_connection), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Invalid checkSum", Toast.LENGTH_SHORT).show();
                            }
                            holder.progressBarDownload.setProgress(0);
                            holder.textDownloadProgress.setText("");
                        }
                    };

                    // Listen to download progress update to reflect progress in data and UI, in case adapter is refreshed
                    progressUpdateCallback = new ProgressUpdateCallback() {
                        @Override
                        public void onProgressUpdated(final String progressText, final int progress) {
                            uiHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    downloadStatus.setDownloadProgressText(progressText);
                                    downloadStatus.setDownloadProgress(progress);
                                    appDownloadStatus.set(position, downloadStatus);
                                    holder.textDownloadProgress.setText(downloadStatus.getDownloadProgressText());
                                    holder.progressBarDownload.setProgress(progress);
                                }
                            });
                        }
                    };

                    downloadApplicationService = baseApplication.getRetrofit(progressUpdateCallback).create(DownloadApplicationService.class);
                    Call<ResponseBody> call = downloadApplicationService.downloadApplicationFile(getFileUrl(applicationVersion));
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Timber.i("onResponse");

                            if (response != null) {
                                final Response<ResponseBody> downloadResponse = response;
                                executorService.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        writeResponseBodyToDisk(downloadResponse, applicationVersion,
                                                new WriteToFileCallback() {
                                                    @Override
                                                    public void onWriteToFileDone(final Integer fileSizeInKbsDownloaded,
                                                                                  final String tempApkDir,
                                                                                  final String apkName) {
                                                        Timber.i("onWriteToFileDone");

                                                        // Hide progress indicators
                                                        uiHandler.post(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                holder.progressBarDownload.setProgress(0);
                                                                holder.textDownloadProgress.setText("");
                                                                holder.progressBarDownload.setVisibility(View.GONE);
                                                                holder.textDownloadProgress.setVisibility(View.GONE);

                                                                if ((fileSizeInKbsDownloaded == null) ||
                                                                        (fileSizeInKbsDownloaded <= 0)) {
                                                                    downloadCompleteCallback.onDownloadFailed(fileSizeInKbsDownloaded);
                                                                } else {
                                                                    downloadCompleteCallback.onDownloadCompleted(tempApkDir, apkName);
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Timber.e(t, "onFailure DownloadApplicationService");
                        }
                    });
                }
            });

            holder.btnInstall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Timber.i("btnInstall onClick");

                    // Initiate installation of the latest APK version
                    Timber.i("Installing " + applicationVersion.getApplication().getPackageName() + " (version " + applicationVersion.getVersionCode() + ")...");

                    String fileName = applicationVersion.getApplication().getPackageName() + "-" + applicationVersion.getVersionCode() + ".apk";
                    Timber.i("fileName: " + fileName);

                    String language = UserPrefsHelper.getLocale(context).getLanguage();
                    File apkDirectory = new File(Environment.getExternalStorageDirectory() + "/" + ".elimu-ai/appstore/apks/" + language);

                    final File apkFile = new File(apkDirectory, fileName);
                    Timber.i("apkFile: " + apkFile);

                    // Get all local versions of current APK file for deleting
                    final File[] apkFiles = apkDirectory.listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File file, String name) {
                            return (name.startsWith(applicationVersion.getApplication().getPackageName()) && name.endsWith(".apk"));
                        }
                    });

                    PackageUpdateReceiver.PackageUpdateCallback packageUpdateCallback = new
                            PackageUpdateReceiver.PackageUpdateCallback() {
                                @Override
                                public void onInstallComplete(@NonNull String packageName) {
                                    Timber.i("onInstallComplete, package: " + packageName);

                                    holder.btnDownload.setVisibility(View.GONE);
                                    holder.btnInstall.setVisibility(View.GONE);
                                    executorService.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            for (final File file : apkFiles) {
                                                file.delete();
                                                Timber.i("APK " + file.getAbsolutePath() + " is " + "deleted successfully");
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onUninstallComplete(@NonNull String packageName) {
                                    notifyDataSetChanged();
                                }
                            };
                    packageUpdateReceiver.setPackageUpdateCallback(packageUpdateCallback);


                    // Install APK file
                    // TODO: Check for root access. If root access, install APK without prompting
                    // for user confirmation.
                    if (Build.VERSION.SDK_INT >= 24) {
                        // See https://developer.android.com/guide/topics/permissions/requesting.html#install-unknown-apps
                        Uri apkUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".apk.provider", apkFile);
                        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                        intent.setData(apkUri);
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        context.startActivity(intent);
                    } else {
                        Uri apkUri = Uri.fromFile(apkFile);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }
            });
        }
    }

    /**
     * Write downloaded content to file on disk
     *
     * @param response The API response
     * @param applicationVersion The application version of downloaded file
     * @param writeToFileCallback The callback triggered when writing is done, to update progress UI
     * @return Downloaded file size
     */
    private Integer writeResponseBodyToDisk(Response<ResponseBody> response, ApplicationVersion applicationVersion, WriteToFileCallback writeToFileCallback) {
        Timber.i("writeResponseBodyToDisk");

        Integer fileSizeInKbsDownloaded = 0;
        String fileName = applicationVersion.getApplication().getPackageName() + "-" + applicationVersion.getVersionCode() + ".apk";
        Timber.i("fileName: " + fileName);
        File apkDirectory = new File(Environment.getExternalStorageDirectory() + "/" + ".elimu-ai/appstore/apks/");
        Timber.i("apkDirectory: " + apkDirectory);

        if (!apkDirectory.exists()) {
            apkDirectory.mkdirs();
        }

        File apkFile = new File(apkDirectory, fileName);
        Timber.i("apkFile: " + apkFile);
        Timber.i("apkFile.exists(): " + apkFile.exists());

        // Download if apkFile does not exist, or re-download if apkFile is existing but corrupted
        if (!apkFile.exists() || (!applicationVersion.getChecksumMd5().equals(ChecksumHelper.calculateMd5(apkFile)))) {
            FileOutputStream fileOutputStream = null;
            String downloadedApkChecksum = "";

            try {
                fileOutputStream = new FileOutputStream(apkFile);
                InputStream inputStream;

                if (response.isSuccessful()) {
                    inputStream = response.body().byteStream();
                } else {
                    String errorResponse = response.errorBody().string();
                    Timber.w("errorResponse: " + errorResponse);
                    return 0;
                }

                byte[] buffer = new byte[1024];
                int bytesRead = 0;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                    fileSizeInKbsDownloaded += (bytesRead / 1024);
                }
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

                // Delete downloaded APK file in case its checksum is invalid
                if (!downloadedApkChecksum.equals(applicationVersion.getChecksumMd5())) {
                    Timber.w("Invalid checksum. Deleting downloaded APK file: " + apkFile);
                    apkFile.delete();
                    fileSizeInKbsDownloaded = -1;
                }
            }

            Timber.i("fileSizeInKbsDownloaded: " + fileSizeInKbsDownloaded);
        }
        writeToFileCallback.onWriteToFileDone(fileSizeInKbsDownloaded, apkDirectory.getAbsolutePath(), fileName);
        return fileSizeInKbsDownloaded;
    }

    @Override
    public int getItemCount() {
        if (applications != null) {
            return applications.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Timber.i("onCreateViewHolder");

        context = parent.getContext();
        uiHandler = new Handler();
        BaseApplication baseApplication = (BaseApplication) context.getApplicationContext();
        applicationVersionDao = baseApplication.getDaoSession().getApplicationVersionDao();

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_app_list_item, parent, false);
        return new ViewHolder(view);
    }

    public List<Application> getData() {
        return this.applications;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewTitle;

        private final TextView textViewVersion;

        private final Button btnDownload;

        private final Button btnInstall;

        private final ProgressBar progressBarDownload;

        private final TextView textDownloadProgress;

        private final ImageView imageAppIcon;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewVersion = itemView.findViewById(R.id.textViewVersion);
            btnDownload = itemView.findViewById(R.id.buttonDownload);
            btnInstall = itemView.findViewById(R.id.buttonInstall);
            progressBarDownload = itemView.findViewById(R.id.progressBarDownloadProgress);
            textDownloadProgress = itemView.findViewById(R.id.textViewDownloadProgress);
            imageAppIcon = itemView.findViewById(R.id.iv_app_icon);
        }
    }

    /**
     * Get download file url
     *
     * @param applicationVersion The application version info of the download file
     * @return The file url
     */
    private String getFileUrl(ApplicationVersion applicationVersion) {
        Timber.i("getFileUrl");

        Timber.i("applicationVersion.getApplication(): " + applicationVersion.getApplication());
        Timber.i("applicationVersion.getFileSizeInKb(): " + applicationVersion.getFileSizeInKb());
        Timber.i("applicationVersion.getFileUrl(): " + applicationVersion.getFileUrl());
        Timber.i("applicationVersion.getContentType(): " + applicationVersion.getContentType());
        Timber.i("applicationVersion.getVersionCode(): " + applicationVersion.getVersionCode());
        Timber.i("applicationVersion.getStartCommand(): " + applicationVersion.getStartCommand());
        Timber.i("applicationVersion.getTimeUploaded().getTime(): " + applicationVersion.getTimeUploaded().getTime());

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

        String fileName = applicationVersion.getApplication().getPackageName() + "-" + applicationVersion.getVersionCode() + ".apk";
        Timber.i("fileName: " + fileName);

        Timber.i("Downloading APK: " + applicationVersion.getApplication().getPackageName() + " (version " + applicationVersion.getVersionCode() + ", " + applicationVersion.getFileSizeInKb() + "kB)");

        return fileUrl;
    }

    interface WriteToFileCallback {

        void onWriteToFileDone(Integer fileSizeInKbsDownloaded, String tempApkDir, String apkName);
    }
}
