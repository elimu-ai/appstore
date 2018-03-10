package ai.elimu.appstore.util;

import android.content.Context;
import android.os.Environment;

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

import timber.log.Timber;

/**
 * Downloads APK files from the web server.
 *
 * TODO: verify file by its checksum.
 */
public class ApkLoader {

    public static File loadApk(String urlValue, String fileName, Context context) {
        Timber.i("loadApk");

        Timber.i("Downloading from " + urlValue + "...");

        String language = Locale.getDefault().getLanguage();
        File apkDirectory = new File(Environment.getExternalStorageDirectory() + "/.elimu-ai/appstore/apks/" + language);
        Timber.i("apkDirectory: " + apkDirectory);
        if (!apkDirectory.exists()) {
            apkDirectory.mkdirs();
        }

        File apkFile = new File(apkDirectory, fileName);
        Timber.i("apkFile: " + apkFile);
        Timber.i("apkFile.exists(): " + apkFile.exists());

        if (!apkFile.exists()) {
            FileOutputStream fileOutputStream = null;
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
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String errorResponse = "";
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        errorResponse += line;
                    }
                    Timber.w("errorResponse: " + errorResponse);
                    return null;
                }

                byte[] bytes = IOUtils.toByteArray(inputStream);
                fileOutputStream = new FileOutputStream(apkFile);
                fileOutputStream.write(bytes);
                fileOutputStream.flush();
            } catch (MalformedURLException e) {
                Timber.e(e, "MalformedURLException");
            } catch (IOException e) {
                Timber.e(e, "IOException");
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        Timber.i(e, "IOException");
                    }
                }
            }
        }

        return apkFile;
    }
}
