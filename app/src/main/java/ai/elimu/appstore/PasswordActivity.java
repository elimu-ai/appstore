package ai.elimu.appstore;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PasswordActivity extends AppCompatActivity {

    public static final String PREF_PASSWORD = "pref_password";

    private EditText mEditTextPassword;
    private Button mButtonPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(getClass().getName(), "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_password);

        mEditTextPassword = (EditText) findViewById(R.id.editTextPassword);
        mButtonPassword = (Button) findViewById(R.id.buttonPassword);
    }

    @Override
    protected void onStart() {
        Log.i(getClass().getName(), "onStart");
        super.onStart();

        mEditTextPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                Log.i(getClass().getName(), "onKey");

                if (TextUtils.isEmpty(mEditTextPassword.getText().toString())) {
                    mButtonPassword.setEnabled(false);
                } else {
                    mButtonPassword.setEnabled(true);
                }

                return false;
            }
        });

        mButtonPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(getClass().getName(), "onClick");

                String password = mEditTextPassword.getText().toString();

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                sharedPreferences.edit().putString(PREF_PASSWORD, password).commit();

                finish();
            }
        });
    }
}
