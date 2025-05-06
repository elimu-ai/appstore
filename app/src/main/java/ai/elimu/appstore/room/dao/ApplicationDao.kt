package ai.elimu.appstore.room.dao;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ai.elimu.appstore.room.entity.Application;

@Dao
public interface ApplicationDao {

    @Insert
    void insert(Application application);

    @Query("SELECT * FROM Application a WHERE a.id = :id")
    Application load(Long id);

    @Query("SELECT * FROM Application a")
    List<Application> loadAll();

    @Query("SELECT * FROM Application a")
    Cursor loadAllAsCursor();

    @Update
    void update(Application application);
}
