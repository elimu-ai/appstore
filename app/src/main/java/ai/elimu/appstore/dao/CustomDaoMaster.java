package ai.elimu.appstore.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.database.Database;

import ai.elimu.appstore.model.Application;
import timber.log.Timber;

public class CustomDaoMaster extends DaoMaster {

    public CustomDaoMaster(Database db) {
        super(db);
        Timber.i("CustomDaoMaster");
    }

    public static class DevOpenHelper extends OpenHelper {

        public DevOpenHelper(Context context, String name) {
            super(context, name);
        }

        public DevOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(Database db, int oldVersion, int newVersion) {
            Timber.i("Upgrading schema from version " + oldVersion + " to " + newVersion);

            if (oldVersion < 1003004) {
                dropAllTables(db, true);
                onCreate(db);
            }

            if (oldVersion < 2000003) {
                DbMigrationHelper.migrate(db, ApplicationVersionDao.class);
            }

            if (oldVersion < 2000011) {
                // Add checksumMd5
                DbMigrationHelper.migrate(db, ApplicationVersionDao.class);
            }

            if (oldVersion < 2001000) {
                // Add listOrder
                DbMigrationHelper.migrate(db, ApplicationDao.class);
            }

            if (oldVersion < 2001002) {
                // Remove NOT NULL restriction for listOrder
                DbMigrationHelper.migrate(db, ApplicationDao.class);
            }

            if (oldVersion < 2002000) {
                // Add ApplicationVersion#versionName and ApplicationVersion#label
                // Drop and re-create entire database since property values of existing ApplicationVersions in the database are not overwritten
                dropAllTables(db, true);
                onCreate(db);
            }

            if (oldVersion < 2002002) {
                // Add minSdkVersion
                DbMigrationHelper.migrate(db, ApplicationVersionDao.class);
            }
        }
    }
}
