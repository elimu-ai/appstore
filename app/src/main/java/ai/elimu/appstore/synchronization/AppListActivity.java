package ai.elimu.appstore.synchronization;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.AbstractQueue;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import ai.elimu.appstore.BaseApplication;
import ai.elimu.appstore.R;
import ai.elimu.appstore.dao.ApplicationDao;
import ai.elimu.appstore.dao.ApplicationVersionDao;
import ai.elimu.appstore.model.Application;
import ai.elimu.appstore.model.ApplicationVersion;
import ai.elimu.appstore.model.DownloadTaskInfo;
import ai.elimu.appstore.receiver.PackageUpdateReceiver;
import ai.elimu.appstore.service.DownloadApplicationService;
import ai.elimu.appstore.service.DownloadCompleteCallback;
import ai.elimu.appstore.service.ProgressUpdateCallback;
import ai.elimu.appstore.service.PrepareDownloadAllCallback;
import ai.elimu.appstore.util.AppPrefs;
import ai.elimu.appstore.util.ChecksumHelper;
import ai.elimu.appstore.util.ConnectivityHelper;
import ai.elimu.appstore.util.FileUtils;
import ai.elimu.appstore.util.UserPrefsHelper;
import ai.elimu.model.enums.Locale;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Loads the Applications stored in the SQLite database and displays them in a list.
 */
public class AppListActivity extends AppCompatActivity implements View.OnClickListener {

    private final String DATE_FORMAT_LAST_SYNC = "yyyy-MM-dd HH:mm";

    private TextView textViewLastSynchronization;

    private List<Application> applications;

    private AppListAdapter appListAdapter;

    private RecyclerView appListRecyclerView;

    private ApplicationDao applicationDao;

    private PackageUpdateReceiver packageUpdateReceiver;

    private ProgressBar progressBarLoading;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private ExecutorService downloadAllWorker = Executors.newSingleThreadExecutor();

    private ApplicationVersionDao applicationVersionDao;

    private String language;

    private Handler uiHandler = new Handler();

    private AbstractQueue<DownloadTaskInfo> downloadAllTaskQueue;

    private PrepareDownloadAllCallback prepareDownloadCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        BaseApplication baseApplication = (BaseApplication) getApplication();

        applicationDao = baseApplication.getDaoSession().getApplicationDao();
        applicationVersionDao = baseApplication.getDaoSession().getApplicationVersionDao();

        initPackageUpdateReceiver();
        initViews();
    }

    /**
     * Initialize install/uninstall completion receiver
     */
    private void initPackageUpdateReceiver() {
        packageUpdateReceiver = new PackageUpdateReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_INSTALL_PACKAGE);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        registerReceiver(packageUpdateReceiver, intentFilter);
    }

    /**
     * Initialize main views of app list activity
     */
    private void initViews() {
        appListRecyclerView = findViewById(R.id.recycler_view_apps);
        appListRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        appListRecyclerView.setLayoutManager(layoutManager);

        textViewLastSynchronization = findViewById(R.id.textViewLastSynchronization);
        progressBarLoading = findViewById(R.id.progress_bar_loading);

        // Display the time of last synchronization with the server
        long timeOfLastSynchronization = AppPrefs.getLastSyncTime();
        Date date = new Date(timeOfLastSynchronization);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_LAST_SYNC);
        String dateAsString = simpleDateFormat.format(date);
        textViewLastSynchronization.setText(String.format(getString(R.string
                .last_synchronization), dateAsString));

        // Load the list of Applications stored in the local database
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // Load Applications from database, sorted in the same order as received from the server
                applications = applicationDao.queryBuilder()
                        .orderAsc(ApplicationDao.Properties.ListOrder)
                        .list();

                // Initialize list adapter
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Timber.i("applications.size(): " + applications.size());
                        BaseApplication baseApplication = (BaseApplication) getApplication();
                        appListAdapter = new AppListAdapter(applications, packageUpdateReceiver, baseApplication);
                        appListRecyclerView.setAdapter(appListAdapter);

                        //Hide loading dialog
                        progressBarLoading.setVisibility(View.GONE);
                    }
                });
            }
        });

        findViewById(R.id.btn_download_all).setOnClickListener(this);

    }

    /**
     * Prepare download task for an application
     *
     * @param applications The application list
     * @param position     The position of being-downloaded application within application list
     */
    private void prepareDownloadTasks(List<Application> applications, final int position) {
        if (!ConnectivityHelper.isNetworkAvailable(this)) {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(AppListActivity.this, getString(R.string.app_list_check_internet_connection),
                            Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        final Application application = applications.get(position);
        final PackageManager packageManager = getPackageManager();

        // Fetch the latest APK version
        List<ApplicationVersion> applicationVersions = applicationVersionDao.queryBuilder()
                .where(ApplicationVersionDao.Properties.ApplicationId.eq(application.getId()))
                .orderDesc(ApplicationVersionDao.Properties.VersionCode)
                .list();

        final ApplicationVersion applicationVersion = applicationVersions.get(0);

        // Check if the APK file has already been downloaded to the SD card
        String fileName = applicationVersion.getApplication().getPackageName() + "-" +
                applicationVersion.getVersionCode() + ".apk";
        File apkDirectory = new File(FileUtils.getApkFolderPath(language));
        final File existingApkFile = new File(apkDirectory, fileName);

        //Check if app was installed or not
        boolean isAppInstalled = true;
        try {
            packageManager.getApplicationInfo(application.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            isAppInstalled = false;
        }

        //Only download if app is not installed & apk doesn't exist, or existing but with invalid checksum
        if ((!existingApkFile.exists() || (!applicationVersion.getChecksumMd5().equals(ChecksumHelper
                .calculateMd5(existingApkFile))))
                && !isAppInstalled) {

            appListAdapter.getAppDownloadStatus().get(position).setDownloading(true);

            final RecyclerView.ViewHolder holder = appListRecyclerView.findViewHolderForAdapterPosition(position);
            refreshAdapter();

            //Listen to download completed event to update app icon
            final DownloadCompleteCallback downloadCompleteCallback = new DownloadCompleteCallback() {
                @Override
                public void onDownloadCompleted(@NonNull String tempApkDir, @NonNull String apkName) {

                    //Move downloaded file to correct folder
                    File correctApkDirectory = new File(FileUtils.getApkFolderPath(language));

                    File srcFile = new File(tempApkDir, apkName);
                    File dstFile = new File(correctApkDirectory, apkName);

                    try {
                        FileUtils.moveFile(srcFile, dstFile);

                        //Change visibility of download/install buttons upon moving completion
                        appListAdapter.getAppDownloadStatus().get(position).setDownloading(false);

                        //Set app icon upon download completion
                        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(existingApkFile
                                .getAbsolutePath(), 0);
                        if (packageInfo != null) {
                            packageInfo.applicationInfo.sourceDir = existingApkFile.getAbsolutePath();
                            packageInfo.applicationInfo.publicSourceDir = existingApkFile.getAbsolutePath();
                            final Drawable appIcon = packageInfo.applicationInfo.loadIcon(packageManager);

                            if (holder instanceof AppListAdapter.ViewHolder) {
                                uiHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((AppListAdapter.ViewHolder) holder).getImageAppIcon().setImageDrawable
                                                (appIcon);
                                    }
                                });
                            }

                        }
                    } catch (IOException e) {
                        Timber.e(e);
                    }

                    if (holder instanceof AppListAdapter.ViewHolder) {
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                refreshAdapter();
                            }
                        });
                    }

                }

                @Override
                public void onDownloadFailed(Integer fileSizeInKbsDownloaded) {
                    appListAdapter.getAppDownloadStatus().get(position).setDownloading(false);

                    final String downloadFailedMessage;
                    if (fileSizeInKbsDownloaded == 0 || fileSizeInKbsDownloaded == null) {
                        downloadFailedMessage = AppListActivity.this.getString(R.string
                                .app_list_check_internet_connection);
                    } else {
                        downloadFailedMessage = AppListActivity.this.getString(R.string.invalid_checksum);
                    }
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            //Show error message and hide download progress bar & text
                            Toast.makeText(AppListActivity.this, downloadFailedMessage, Toast.LENGTH_SHORT).show();
                            refreshAdapter();
                        }
                    });
                }
            };

            //Listen to download progress update to reflect progress in data and UI, in case adapter is refreshed
            ProgressUpdateCallback progressUpdateCallback = new ProgressUpdateCallback() {
                @Override
                public void onProgressUpdated(final String progressText, final int progress) {
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            appListAdapter.getAppDownloadStatus().get(position).setDownloadProgressText
                                    (progressText);
                            appListAdapter.getAppDownloadStatus().get(position).setDownloadProgress(progress);
                            RecyclerView.ViewHolder holder = appListRecyclerView
                                    .findViewHolderForAdapterPosition(position);
                            if (holder instanceof AppListAdapter.ViewHolder) {

                                //Refresh progress bar and text
                                AppListAdapter.ViewHolder appListHolder = (AppListAdapter.ViewHolder) holder;
                                appListHolder.getProgressBarDownload().setProgress(progress);
                                appListHolder.getTextDownloadProgress().setText(progressText);
                            }
                        }
                    });
                }
            };

            //Create download service
            DownloadApplicationService downloadApplicationService = ((BaseApplication) getApplicationContext())
                    .getRetrofit(progressUpdateCallback)
                    .create(DownloadApplicationService.class);
            Call<ResponseBody> call = downloadApplicationService.downloadApplicationFile(AppListAdapter
                    .getFileUrl(applicationVersion, AppListActivity.this));

            //Queue download task
            downloadAllTaskQueue.add(new DownloadTaskInfo(call, applicationVersion, downloadCompleteCallback));
        }

        if ((position == applications.size() - 1) && prepareDownloadCallback != null) {
            prepareDownloadCallback.onPrepareCompleted();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(packageUpdateReceiver);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_download_all:

                //Prepare download tasks for all applications
                downloadAllTaskQueue = new LinkedBlockingQueue<>();
                Locale locale = UserPrefsHelper.getLocale(this);
                if (locale == null) {
                    // The user typed a License for a custom Project, which does not use a specific Locale.
                    // Fall back to English
                    locale = Locale.EN;
                }

                language = locale.getLanguage();

                prepareDownloadCallback = new PrepareDownloadAllCallback() {
                    @Override
                    public void onPrepareCompleted() {
                        //Start executing prepared download tasks from queue
                        if (downloadAllTaskQueue.size() > 0) {
                            executeDownloadTask(downloadAllTaskQueue.remove());
                        }
                    }
                };

                downloadAllWorker.execute(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < applications.size(); i++) {
                            prepareDownloadTasks(applications, i);
                        }
                    }
                });

                break;

            default:
                break;

        }
    }

    /**
     * Execute a queued download task
     *
     * @param downloadTaskInfo The queued download task
     */
    private void executeDownloadTask(@NonNull final DownloadTaskInfo downloadTaskInfo) {
        downloadAllWorker.execute(new Runnable() {
            @Override
            public void run() {
                downloadTaskInfo.getCall().enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response != null) {
                            final Response<ResponseBody> downloadResponse = response;
                            executorService.execute(new Runnable() {
                                @Override
                                public void run() {
                                    AppListAdapter.writeResponseBodyToDisk(downloadResponse, downloadTaskInfo
                                                    .getApplicationVersion(),
                                            new AppListAdapter.WriteToFileCallback() {
                                                @Override
                                                public void onWriteToFileDone(final Integer fileSizeInKbsDownloaded,
                                                                              final String tempApkDir,
                                                                              final String apkName) {
                                                    // Hide progress indicators
                                                    uiHandler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            refreshAdapter();
                                                            if ((fileSizeInKbsDownloaded == null) || (fileSizeInKbsDownloaded <= 0)) {
                                                                downloadTaskInfo.getDownloadCompleteCallback()
                                                                        .onDownloadFailed(fileSizeInKbsDownloaded);
                                                            } else {
                                                                downloadTaskInfo.getDownloadCompleteCallback()
                                                                        .onDownloadCompleted(tempApkDir, apkName);
                                                            }

                                                            //Continue the next download from queue
                                                            if (downloadAllTaskQueue.size() > 0) {
                                                                executeDownloadTask(downloadAllTaskQueue.remove());
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
    }

    private void refreshAdapter() {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                appListAdapter.notifyDataSetChanged();
            }
        });
    }
}
