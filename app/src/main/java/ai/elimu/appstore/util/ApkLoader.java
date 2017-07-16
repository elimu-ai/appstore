package ai.elimu.appstore.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

/**
 * Downloads APK files from the web server.
 */
public class ApkLoader {

    public static File loadApk(String urlValue, String fileName, Context context) {
        Log.i(ApkLoader.class.getName(), "loadApk");

        Log.i(ApkLoader.class.getName(), "Downloading from " + urlValue + "...");

        String language = Locale.getDefault().getLanguage();
        File apkDirectory = new File(Environment.getExternalStorageDirectory() + "/.elimu-ai/appstore/apks/" + language);
        Log.i(ApkLoader.class.getName(), "apkDirectory: " + apkDirectory);
        if (!apkDirectory.exists()) {
            apkDirectory.mkdirs();
        }

        File apkFile = new File(apkDirectory, fileName);
        Log.i(ApkLoader.class.getName(), "apkFile: " + apkFile);
        Log.i(ApkLoader.class.getName(), "apkFile.exists(): " + apkFile.exists());

        if (!apkFile.exists()) {
            FileOutputStream fileOutputStream = null;
            try {
                URL url = new URL(urlValue);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                int responseCode = httpURLConnection.getResponseCode();
                Log.i(ApkLoader.class.getName(), "responseCode: " + responseCode);
                InputStream inputStream = null;
                if (responseCode == 200) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String errorResponse = "";
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        errorResponse += line;
                    }
                    Log.w(ApkLoader.class.getName(), "errorResponse: " + errorResponse);
                    return null;
                }

                byte[] bytes = IOUtils.toByteArray(inputStream);
                fileOutputStream = new FileOutputStream(apkFile);
                fileOutputStream.write(bytes);
                fileOutputStream.flush();
            } catch (MalformedURLException e) {
                Log.e(ApkLoader.class.getName(), "MalformedURLException", e);
            } catch (IOException e) {
                Log.e(ApkLoader.class.getName(), "IOException", e);
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        Log.e(ApkLoader.class.getName(), "IOException", e);
                    }
                }
            }
        }

        return apkFile;
    }
}
