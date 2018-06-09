package ai.elimu.appstore.synchronization;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ai.elimu.appstore.BaseApplication;
import ai.elimu.appstore.R;
import ai.elimu.appstore.dao.ApplicationDao;
import ai.elimu.appstore.model.Application;
import ai.elimu.appstore.receiver.PackageUpdateReceiver;
import ai.elimu.appstore.util.AppPrefs;
import timber.log.Timber;

/**
 * Loads the Applications stored in the SQLite database and displays them in a list.
 */
public class AppListActivity extends AppCompatActivity {

    private final String DATE_FORMAT_LAST_SYNC = "yyyy-MM-dd HH:mm";

    private TextView textViewLastSynchronization;

    private List<Application> applications;

    private AppListAdapter appListAdapter;

    private RecyclerView appListRecyclerView;

    private ApplicationDao applicationDao;

    private PackageUpdateReceiver packageUpdateReceiver;

    private ProgressBar progressBarLoading;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Handler uiHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        BaseApplication baseApplication = (BaseApplication) getApplication();
        applicationDao = baseApplication.getDaoSession().getApplicationDao();

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

                        // Hide loading dialog
                        progressBarLoading.setVisibility(View.GONE);
                    }
                });
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(packageUpdateReceiver);
    }
}
