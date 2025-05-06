package ai.elimu.appstore.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ai.elimu.appstore.room.dao.ApplicationDao;
import ai.elimu.appstore.room.dao.ApplicationVersionDao;
import ai.elimu.appstore.room.entity.Application;
import ai.elimu.appstore.room.entity.ApplicationVersion;
import timber.log.Timber;

@Database(version = 3, entities = {Application.class, ApplicationVersion.class})
@TypeConverters({EnumConverter.class})
public abstract class RoomDb extends RoomDatabase {

    public abstract ApplicationDao applicationDao();

    public abstract ApplicationVersionDao applicationVersionDao();

    private static volatile RoomDb INSTANCE;

    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    public static RoomDb getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RoomDb.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room
                            .databaseBuilder(
                                    context.getApplicationContext(),
                                    RoomDb.class,
                                    "appstore_db"
                            )
                            .addMigrations(
                                    MIGRATION_1_2,
                                    MIGRATION_2_3
                            )
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            Timber.i("migrate (1 --> 2)");

            String sql = "ALTER TABLE Application ADD COLUMN `literacySkills` TEXT";
            Timber.i("sql: %s", sql);
            database.execSQL(sql);

            sql = "ALTER TABLE Application ADD COLUMN `numeracySkills` TEXT";
            Timber.i("sql: %s", sql);
            database.execSQL(sql);
        }
    };

    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            Timber.i("migrate (2 --> 3)");
            String sql = "CREATE TABLE IF NOT EXISTS `ApplicationVersion` (`applicationId` INTEGER NOT NULL, `fileUrl` TEXT NOT NULL, `fileSizeInKb` INTEGER NOT NULL, `checksumMd5` TEXT NOT NULL, `versionCode` INTEGER NOT NULL, `id` INTEGER, PRIMARY KEY(`id`))";
            Timber.i("sql: %s", sql);
            database.execSQL(sql);
        }
    };
}
