package ai.elimu.appstore.room.dao;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ai.elimu.appstore.room.entity.ApplicationVersion;

@Dao
public interface ApplicationVersionDao {

    @Insert
    void insert(ApplicationVersion applicationVersion);

    @Query("SELECT * FROM ApplicationVersion av WHERE av.id = :id")
    ApplicationVersion load(Long id);

    @Query("SELECT * FROM ApplicationVersion av")
    List<ApplicationVersion> loadAll();

    @Update
    void update(ApplicationVersion applicationVersion);

    @Query("DELETE FROM ApplicationVersion WHERE applicationId = :applicationId")
    void delete(Long applicationId);
}
