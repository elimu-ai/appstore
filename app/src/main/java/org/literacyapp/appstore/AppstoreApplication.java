package org.literacyapp.appstore;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.content.ContextCompat;

import org.apache.log4j.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.mindpipe.android.logging.log4j.LogConfigurator;

public class AppstoreApplication extends Application {

    private Logger logger;

//    private SQLiteDatabase db;
//    private DaoMaster daoMaster;
//    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        int permissionCheckWriteExternalStorage = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheckWriteExternalStorage == PackageManager.PERMISSION_GRANTED) {
            // Create directory structure
            File logDirectory = new File(Environment.getExternalStorageDirectory() + File.separator + "Appstore");
            if (!logDirectory.exists()) {
                logDirectory.mkdirs();
            }

            // Configure Log4J
            LogConfigurator logConfigurator = new LogConfigurator();
            logConfigurator.setFileName(logDirectory + File.separator + "logs" + File.separator + "appstore.log." + new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
            logConfigurator.setRootLevel(org.apache.log4j.Level.DEBUG);
            logConfigurator.setLevel("org.apache", org.apache.log4j.Level.ERROR);
            logConfigurator.setFilePattern("%d %-5p [%c{2}]-[%L] %m%n");
            logConfigurator.setMaxFileSize(1024 * 1024 * 5); // 5MB
            logConfigurator.setImmediateFlush(true);
            logConfigurator.configure();
            logger = Logger.getLogger(getClass());
            logger.info("onCreate");
        }

        // TODO: greenDAO config
    }
}
