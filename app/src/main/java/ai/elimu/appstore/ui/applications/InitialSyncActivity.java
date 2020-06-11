package ai.elimu.appstore.ui.applications;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import ai.elimu.appstore.BaseApplication;
import ai.elimu.appstore.R;
import ai.elimu.appstore.rest.ApplicationsService;
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

                // TODO
            }

            @Override
            public void onFailure(Call<List<ApplicationGson>> call, Throwable t) {
                Timber.i("onFailure");

                // TODO
            }
        });
    }
}
