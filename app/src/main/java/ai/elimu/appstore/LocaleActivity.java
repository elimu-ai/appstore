package ai.elimu.appstore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import ai.elimu.appstore.util.RootHelper;
import ai.elimu.model.enums.Locale;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LocaleActivity extends AppCompatActivity {

    public static final String PREF_LOCALE = "pref_locale";

    private Spinner spinnerLocale;

    private Button buttonLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(getClass().getName(), "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_locale);

        spinnerLocale = (Spinner) findViewById(R.id.spinnerLocale);
        buttonLocale = (Button) findViewById(R.id.buttonLocale);
    }

    @Override
    protected void onStart() {
        Log.i(getClass().getName(), "onStart");
        super.onStart();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (Locale locale : Locale.values()) {
            int resourceIdentifier = getResources().getIdentifier("language_" + locale.toString().toLowerCase(), "string", getPackageName());
            String language = getString(resourceIdentifier);
            arrayAdapter.add(locale.toString().toLowerCase() + " - " + language);
        }

        spinnerLocale.setAdapter(arrayAdapter);

        // Auto-select locale of device
        java.util.Locale localeOfDevice = java.util.Locale.getDefault();
        Log.i(getClass().getName(), "localeOfDevice: " + localeOfDevice);
        for (Locale locale : Locale.values()) {
            if (locale.getLanguage().equals(localeOfDevice.getLanguage())) {
                spinnerLocale.setSelection(locale.ordinal());
            }
        }

        buttonLocale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(getClass().getName(), "onClick");

                Log.i(getClass().getName(), "spinnerLocale.getSelectedItem(): " + spinnerLocale.getSelectedItem());
                Locale localeSelected = Locale.values()[spinnerLocale.getSelectedItemPosition()];
                Log.i(getClass().getName(), "localeSelected: " + localeSelected);

                // TODO: if root, set locale of device?

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                sharedPreferences.edit().putString(PREF_LOCALE, localeSelected.toString()).commit();

                // Restart application
                Intent intent = getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}
