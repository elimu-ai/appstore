package ai.elimu.appstore.ui.applications;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.List;

import ai.elimu.appstore.R;
import ai.elimu.appstore.room.RoomDb;
import ai.elimu.appstore.room.dao.ApplicationDao;
import ai.elimu.appstore.room.dao.ApplicationVersionDao;
import ai.elimu.appstore.room.entity.Application;
import ai.elimu.appstore.room.entity.ApplicationVersion;
import timber.log.Timber;

public class ApplicationListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i("onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_application_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CollapsingToolbarLayout toolBarLayout = findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(view -> {
//            Timber.i("onClick");
//            Snackbar.make(view, "Synchronizing...", Snackbar.LENGTH_LONG).show();
//            // TODO: Download list of Applications from REST API
//        });
    }

    @Override
    protected void onStart() {
        Timber.i("onStart");
        super.onStart();

        // Configure list adapter
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        final ApplicationListAdapter applicationListAdapter = new ApplicationListAdapter(this);
        recyclerView.setAdapter(applicationListAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        // Fetch all Applications from database, and update the list adapter
        RoomDb roomDb = RoomDb.getDatabase(getApplicationContext());
        ApplicationDao applicationDao = roomDb.applicationDao();
        ApplicationVersionDao applicationVersionDao = roomDb.applicationVersionDao();
        RoomDb.databaseWriteExecutor.execute(() -> {
            List<Application> applications = applicationDao.loadAll();
            Log.d(getClass().getName(), "applications.size(): " + applications.size());
            if(null!=applications) {
                applicationListAdapter.setApplications(applications);
            }
            List<ApplicationVersion> applicationVersions = applicationVersionDao.loadAll();
            Log.d(getClass().getName(), "applicationVersions.size(): " + applicationVersions.size());
            applicationListAdapter.setApplicationVersions(applicationVersions);


            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    applicationListAdapter.notifyDataSetChanged();
                }
            });

        });
    }
}