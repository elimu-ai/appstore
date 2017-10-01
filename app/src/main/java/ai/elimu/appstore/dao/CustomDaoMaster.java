package ai.elimu.appstore.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.greenrobot.greendao.database.Database;

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

            if (oldVersion < 1003002) {
                // Upgrade to schemaVersion 1003002
                DbMigrationHelper.migrate(db, ApplicationDao.class);
            }

            if (oldVersion < 1003004) {
                dropAllTables(db, true);
                onCreate(db);
            }

            if (oldVersion < 2000003) {
                DbMigrationHelper.migrate(db, ApplicationVersionDao.class);
            }
        }
    }
}
