package org.literacyapp.appstore;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.mindpipe.android.logging.log4j.LogConfigurator;

public class AppstoreApplication extends Application {

    private Logger logger = Logger.getLogger(getClass().getName());

    @Override
    public void onCreate() {
        Logger.getLogger(getClass().getName()).log(Level.INFO, "onCreate");
        super.onCreate();

        int permissionCheckWriteExternalStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        logger.log(Level.INFO, "permissionCheckWriteExternalStorage: " + permissionCheckWriteExternalStorage);
        if (permissionCheckWriteExternalStorage != PackageManager.PERMISSION_GRANTED) {
            // Since permission requests require an Activity, return to MainActivity and trigger permission requests from there
            return;
        }

        // Create directory structure
        File logDirectory = new File(Environment.getExternalStorageDirectory() + File.separator + "Appstore");
        logger.log(Level.INFO, "logDirectory: " + logDirectory);
        logger.log(Level.INFO, "logDirectory.exists(): " + logDirectory.exists());
        if (!logDirectory.exists()) {
            boolean success = logDirectory.mkdirs();
            Logger.getLogger(getClass().getName()).log(Level.INFO, "logDirectory.mkdirs(): " + success);
        }

        // Configure Log4J
        LogConfigurator logConfigurator = new LogConfigurator();
        logConfigurator.setFileName(logDirectory + File.separator + "appstore.log." + new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
        logConfigurator.setRootLevel(org.apache.log4j.Level.DEBUG);
        logConfigurator.setLevel("org.apache", org.apache.log4j.Level.ERROR);
        logConfigurator.setFilePattern("%d %-5p [%c{2}]-[%L] %m%n");
        logConfigurator.setMaxFileSize(1024 * 1024 * 5); // 5MB
        logConfigurator.setImmediateFlush(true);
        logConfigurator.configure();
    }
}
