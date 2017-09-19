package ai.elimu.appstore.synchronization;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ai.elimu.appstore.BaseApplication;
import ai.elimu.appstore.R;
import ai.elimu.appstore.dao.ApplicationDao;
import ai.elimu.appstore.model.Application;
import timber.log.Timber;

public class AppListActivity extends AppCompatActivity {

    private TextView textViewLastSynchronization;

    private List<Application> applications;

    private ArrayAdapter arrayAdapter;

    private ListView listViewApplications;

    private ApplicationDao applicationDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i("onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_app_list);

        listViewApplications = findViewById(R.id.listViewApplications);

        textViewLastSynchronization = findViewById(R.id.textViewLastSynchronization);

        BaseApplication baseApplication = (BaseApplication) getApplication();
        applicationDao = baseApplication.getDaoSession().getApplicationDao();
    }

    @Override
    protected void onStart() {
        Timber.i("onStart");
        super.onStart();

        // Display the time of last synchronization with the server
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        long timeOfLastSynchronization = sharedPreferences.getLong(AppSynchronizationActivity.DownloadAppListAsyncTask.PREF_LAST_SYNCHRONIZATION, 0);
        Date date = new Date(timeOfLastSynchronization);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateAsString = simpleDateFormat.format(date);
        String lastSynchronization = getString(R.string.last_synchronization);
        textViewLastSynchronization.setText(lastSynchronization + ": " + dateAsString);

        // Load the list of Applications stored in the local database
        applications = applicationDao.loadAll();
        Timber.i("applications.size(): " + applications.size());
        arrayAdapter = new AppListArrayAdapter(getBaseContext(), R.layout.activity_app_list_item, applications);
        listViewApplications.setAdapter(arrayAdapter);


    }
}
