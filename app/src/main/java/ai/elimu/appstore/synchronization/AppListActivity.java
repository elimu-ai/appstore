package ai.elimu.appstore.synchronization;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ai.elimu.appstore.BaseApplication;
import ai.elimu.appstore.R;
import ai.elimu.appstore.dao.ApplicationDao;
import ai.elimu.appstore.model.Application;
import ai.elimu.appstore.util.Const;

public class AppListActivity extends AppCompatActivity {

    private TextView mTextViewLastSynchronization;

    private List<Application> mApplications;

    private AppListAdapter mAdapterApps;

    private RecyclerView mRecyclerApps;

    private ApplicationDao mApplicationDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Timber.i("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        BaseApplication baseApplication = (BaseApplication) getApplication();
        mApplicationDao = baseApplication.getDaoSession().getApplicationDao();

        initViews();
    }

    private void initViews() {
        mRecyclerApps = findViewById(R.id.recycler_view_apps);
        mRecyclerApps.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerApps.setLayoutManager(layoutManager);

        mTextViewLastSynchronization = findViewById(R.id.textViewLastSynchronization);
        // Display the time of last synchronization with the server
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (getApplicationContext());
        long timeOfLastSynchronization = sharedPreferences.getLong(AppSynchronizationActivity
                .DownloadAppListAsyncTask.PREF_LAST_SYNCHRONIZATION, 0);
        Date date = new Date(timeOfLastSynchronization);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Const.DATE_FORMAT_LAST_SYNC);
        String dateAsString = simpleDateFormat.format(date);
        mTextViewLastSynchronization.setText(String.format(getString(R.string
                .last_synchronization), dateAsString));

        // Load the list of Applications stored in the local database
        mApplications = mApplicationDao.loadAll();
//        Timber.i("mApplications.size(): " + mApplications.size());
        mAdapterApps = new AppListAdapter(mApplications);
        mRecyclerApps.setAdapter(mAdapterApps);

        mAdapterApps.setOnItemClickListener(new AppListAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {

            }
        });
    }

}
