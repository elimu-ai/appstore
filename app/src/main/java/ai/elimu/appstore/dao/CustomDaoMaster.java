package ai.elimu.appstore.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import org.greenrobot.greendao.database.Database;

import java.io.File;
import java.io.FilenameFilter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ai.elimu.appstore.util.UserPrefsHelper;
import timber.log.Timber;

public class CustomDaoMaster extends DaoMaster {

    public CustomDaoMaster(Database db) {
        super(db);
        Timber.i("CustomDaoMaster");
    }

    public static class DevOpenHelper extends OpenHelper {

        private Context context;

        public DevOpenHelper(Context context, String name) {
            super(context, name);
            this.context = context;
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

            if (oldVersion < 2000010) {
                DbMigrationHelper.migrate(db, ApplicationVersionDao.class);

                /**
                 * Check and delete existing APKs to avoid manually deleting corrupt files before upgrading to version 2.0.9
                 * of the Appstore application
                 */
                deleteExistingApks();
            }
        }

        private void deleteExistingApks() {

            /**
             * Skip checking & deleting APK files in the first start-up of application
             */
            if (UserPrefsHelper.getLocale(context) == null) {
                return;
            }

            ExecutorService executorService = Executors.newSingleThreadExecutor();

            /**
             * Get APKs directory
             */
            String language = UserPrefsHelper.getLocale(context).getLanguage();
            File apkDirectory = new File(Environment.getExternalStorageDirectory() + "/" +
                    ".elimu-ai/appstore/apks/" + language);

            /**
             * Get all APK files for deleting
             */
            final File[] apkFiles = apkDirectory.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File file, String name) {
                    return (name.endsWith(".apk"));
                }
            });

            /**
             * Delete APK files in background thread since this operation is timely unpredictable
             */
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    for (final File file : apkFiles) {
                        file.delete();
                        Timber.i("APK " + file.getAbsolutePath() + " is deleted successfully");
                    }
                }
            });

        }
    }
}
