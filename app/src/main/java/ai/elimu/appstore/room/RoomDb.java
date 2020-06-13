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
import ai.elimu.appstore.room.entity.Application;
import timber.log.Timber;

@Database(version = 2, entities = {Application.class})
@TypeConverters({EnumConverter.class})
public abstract class RoomDb extends RoomDatabase {

    public abstract ApplicationDao applicationDao();

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
                                    MIGRATION_1_2
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
            Timber.i("sql: " + sql);
            database.execSQL(sql);

            sql = "ALTER TABLE Application ADD COLUMN `numeracySkills` TEXT";
            Timber.i("sql: " + sql);
            database.execSQL(sql);
        }
    };
}
