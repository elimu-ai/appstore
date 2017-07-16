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

public class JsonLoader {

    public static String loadJson(String urlValue) {
        Log.i(JsonLoader.class.getName(), "loadJson");

        Log.i(JsonLoader.class.getName(), "Downloading from " + urlValue + "...");

        String jsonResponse = null;

        try {
            URL url = new URL(urlValue);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            int responseCode = httpURLConnection.getResponseCode();
            Log.i(JsonLoader.class.getName(), "responseCode: " + responseCode);
            InputStream inputStream = null;
            if (responseCode == 200) {
                inputStream = httpURLConnection.getInputStream();
            } else {
                inputStream = httpURLConnection.getErrorStream();
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            jsonResponse = bufferedReader.readLine();
        } catch (MalformedURLException e) {
            Log.e(JsonLoader.class.getName(), "MalformedURLException", e);
        } catch (ProtocolException e) {
            Log.e(JsonLoader.class.getName(), "ProtocolException", e);
        } catch (IOException e) {
            Log.e(JsonLoader.class.getName(), "IOException", e);
        }

        return jsonResponse;
    }
}
