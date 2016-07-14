package org.literacyapp.appstore;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.apache.log4j.Logger;
import org.literacyapp.model.enums.Locale;

public class LocaleActivity extends AppCompatActivity {

    public static final String PREF_LOCALE = "pref_locale";

    private Logger logger = Logger.getLogger(getClass());

    private Spinner mSpinnerLocale;

    private Button mButtonLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        logger.info("onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_locale);

        mSpinnerLocale = (Spinner) findViewById(R.id.spinnerLocale);
        mButtonLocale = (Button) findViewById(R.id.buttonLocale);
    }

    @Override
    protected void onStart() {
        logger.info("onStart");
        super.onStart();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (Locale locale : Locale.values()) {
            arrayAdapter.add(locale.toString());
        }

        mSpinnerLocale.setAdapter(arrayAdapter);

        mButtonLocale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logger.info("onClick");

                String localeAsString = mSpinnerLocale.getSelectedItem().toString();
                logger.info("localeAsString: " + localeAsString);
                Locale locale = Locale.valueOf(localeAsString);

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                sharedPreferences.edit().putString(PREF_LOCALE, locale.toString()).commit();

                finish();
            }
        });
    }
}
