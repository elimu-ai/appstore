package org.literacyapp.appstore;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.apache.log4j.Logger;

import java.util.logging.Level;

public class MainActivity extends AppCompatActivity {

    private Logger logger = Logger.getLogger(getClass());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        logger.info("onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        int permissionCheckWriteExternalStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        logger.info("permissionCheckWriteExternalStorage: " + permissionCheckWriteExternalStorage);
        if (permissionCheckWriteExternalStorage != PackageManager.PERMISSION_GRANTED) {
            Intent permissionIntent = new Intent(this, PermissionActivity.class);
            startActivity(permissionIntent);
        }
    }
}
