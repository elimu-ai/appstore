package ai.elimu.appstore.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import timber.log.Timber;

public class JsonLoader {

    public static String loadJson(String urlValue) {
        Timber.i("loadJson");

        Timber.i("Downloading from " + urlValue + "...");

        String jsonResponse = null;

        try {
            URL url = new URL(urlValue);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            int responseCode = httpURLConnection.getResponseCode();
            Timber.i("responseCode: " + responseCode);
            InputStream inputStream = null;
            if (responseCode == 200) {
                inputStream = httpURLConnection.getInputStream();
            } else {
                inputStream = httpURLConnection.getErrorStream();
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            jsonResponse = bufferedReader.readLine();
        } catch (MalformedURLException e) {
            Timber.e(e, "MalformedURLException");
        } catch (ProtocolException e) {
            Timber.e(e, "ProtocolException");
        } catch (IOException e) {
            Timber.e(e, "IOException");
        }

        return jsonResponse;
    }
}
