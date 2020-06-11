package ai.elimu.appstore.ui.applications;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ai.elimu.appstore.BaseApplication;
import ai.elimu.appstore.R;
import ai.elimu.appstore.rest.ApplicationsService;
import ai.elimu.appstore.room.GsonToRoomConverter;
import ai.elimu.appstore.room.RoomDb;
import ai.elimu.appstore.room.dao.ApplicationDao;
import ai.elimu.appstore.room.entity.Application;
import ai.elimu.model.v2.gson.application.ApplicationGson;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import timber.log.Timber;

public class InitialSyncActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i("onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_initial_sync);

        textView = findViewById(R.id.initial_sync_textview);
    }

    @Override
    protected void onStart() {
        Timber.i("onStart");
        super.onStart();

        // Download list of Applications from REST API
        BaseApplication baseApplication = (BaseApplication) getApplication();
        Retrofit retrofit = baseApplication.getRetrofit();
        ApplicationsService applicationsService = retrofit.create(ApplicationsService.class);
        Call<List<ApplicationGson>> call = applicationsService.listApplications();
        Timber.i("call.request(): " + call.request());
        textView.setText("Connecting to " + call.request().url());
        call.enqueue(new Callback<List<ApplicationGson>>() {
            @Override
            public void onResponse(Call<List<ApplicationGson>> call, Response<List<ApplicationGson>> response) {
                Timber.i("onResponse");

                Timber.i("response: " + response);

                // Parse the JSON response
//                Snackbar.make(textView, "Synchronizing database...", Snackbar.LENGTH_LONG).show();
                List<ApplicationGson> applicationGsons = response.body();
                Timber.i("applicationGsons.size(): " + applicationGsons.size());
                if (applicationGsons.size() > 0) {
                    processResponseBody(applicationGsons);
                }
            }

            @Override
            public void onFailure(Call<List<ApplicationGson>> call, Throwable t) {
                Timber.e(t, "onFailure");

                Timber.e(t, "t.getCause(): " + t.getCause());

                // Handle error
                Snackbar.make(textView, t.getCause().toString(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void processResponseBody(List<ApplicationGson> applicationGsons) {
        Timber.i("processResponseBody");

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Timber.i("run");

                RoomDb roomDb = RoomDb.getDatabase(getApplicationContext());
                ApplicationDao applicationDao = roomDb.applicationDao();

                for (ApplicationGson applicationGson : applicationGsons) {
                    Timber.i("applicationGson.getId(): " + applicationGson.getId());

                    // Check if the Application has already been stored in the database
                    Application application = applicationDao.load(applicationGson.getId());
                    Timber.i("application: " + application);
                    if (application == null) {
                        // Store the new Application in the database
                        application = GsonToRoomConverter.getApplication(applicationGson);
                        applicationDao.insert(application);
                        Timber.i("Stored Application in database with ID " + application.getId());
                    } else {
                        // Update the existing Application in the database
                        application = GsonToRoomConverter.getApplication(applicationGson);
                        applicationDao.update(application);
                        Timber.i("Updated Application in database with ID " + application.getId());
                    }
                }

                // Redirect to the list of Applications
                Intent intent = new Intent(getApplicationContext(), ApplicationListActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
