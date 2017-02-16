package org.literacyapp.appstore.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.apache.commons.io.FileUtils;
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

/**
 * Downloads APK files from the web server.
 */
public class ApkLoader {

    public static File loadApk(String urlValue, String fileName, Context context) {
        Log.i(ApkLoader.class.getName(), "loadApk");

        Log.i(ApkLoader.class.getName(), "Downloading from " + urlValue + "...");

        File apkDirectory = new File(Environment.getExternalStorageDirectory() + "/.literacyapp-appstore/apks");
        Log.i(ApkLoader.class.getName(), "apkDirectory: " + apkDirectory);
        if (!apkDirectory.exists()) {
            apkDirectory.mkdirs();
        }

        File apkFile = new File(apkDirectory, fileName);
        Log.i(ApkLoader.class.getName(), "apkFile: " + apkFile);
        Log.i(ApkLoader.class.getName(), "apkFile.exists(): " + apkFile.exists());

        if (!apkFile.exists()) {
             try {
                URL url = new URL(urlValue);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                int responseCode = httpURLConnection.getResponseCode();
                Log.i(ApkLoader.class.getName(), "responseCode: " + responseCode);
                if (responseCode == 200) {
                    //read file's chunks and write them to file, instead of writing file to variable
                    //there isn't file size limit
                    //for details visit apache-commons documentation
                    FileUtils.copyURLToFile(url, apkFile);
                } else {
                    InputStream inputStream = httpURLConnection.getErrorStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String errorResponse = "";
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        errorResponse += line;
                    }
                    Log.w(ApkLoader.class.getName(), "errorResponse: " + errorResponse);
                    return null;
                }
            } catch (MalformedURLException e) {
                Log.e(ApkLoader.class.getName(), "MalformedURLException", e);
            } catch (IOException e) {
                Log.e(ApkLoader.class.getName(), "IOException", e);
            }
        }
        return apkFile;
    }
}
