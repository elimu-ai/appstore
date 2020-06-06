package ai.elimu.appstore;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i("onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        Timber.i("onStart");
        super.onStart();
    }
}
