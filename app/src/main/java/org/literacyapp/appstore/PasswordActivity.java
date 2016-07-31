package org.literacyapp.appstore;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.log4j.Logger;

public class PasswordActivity extends AppCompatActivity {

    public static final String PREF_PASSWORD = "pref_password";

    private Logger logger = Logger.getLogger(getClass());

    private EditText mEditTextPassword;
    private Button mButtonPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        logger.info("onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_password);

        mEditTextPassword = (EditText) findViewById(R.id.editTextPassword);
        mButtonPassword = (Button) findViewById(R.id.buttonPassword);
    }

    @Override
    protected void onStart() {
        logger.info("onStart");
        super.onStart();

        mEditTextPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                logger.info("onKey");

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
                logger.info("onClick");

                String password = mEditTextPassword.getText().toString();

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                sharedPreferences.edit().putString(PREF_PASSWORD, password).commit();

                finish();
            }
        });
    }
}
