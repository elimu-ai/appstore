package org.literacyapp.appstore.util;

import android.content.Context;
import android.os.Environment;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ApkLoader {

    private static Logger logger = Logger.getLogger(ApkLoader.class);

    public static File loadApk(String urlValue, String fileName, Context context) {
        logger.info("loadApk");

        logger.info("Downloading from " + urlValue + "...");

        File apkDirectory = new File(Environment.getExternalStorageDirectory() + File.separator + "Appstore" + File.separator + "apks");
        logger.info("apkDirectory: " + apkDirectory);
        if (!apkDirectory.exists()) {
            apkDirectory.mkdirs();
        }

        File apkFile = new File(apkDirectory, fileName);
        logger.info("apkFile: " + apkFile);
        logger.info("apkFile.exists(): " + apkFile.exists());

        if (!apkFile.exists()) {
            FileOutputStream fileOutputStream = null;
            try {
                URL url = new URL(urlValue);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                int responseCode = httpURLConnection.getResponseCode();
                logger.info("responseCode: " + responseCode);
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
                    logger.warn("errorResponse: " + errorResponse);
                    return null;
                }

                byte[] bytes = IOUtils.toByteArray(inputStream);
                fileOutputStream = new FileOutputStream(apkFile);
                fileOutputStream.write(bytes);
                fileOutputStream.flush();
            } catch (MalformedURLException e) {
                logger.error("MalformedURLException", e);
            } catch (IOException e) {
                logger.error("IOException", e);
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        logger.error("IOException", e);
                    }
                }
            }
        }

        return apkFile;
    }
}
