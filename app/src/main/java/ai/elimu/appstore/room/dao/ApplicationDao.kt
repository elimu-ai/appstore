package ai.elimu.appstore.room.dao

import ai.elimu.appstore.room.entity.Application
import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ApplicationDao {
    @Insert
    fun insert(application: Application)

    @Query("SELECT * FROM Application a WHERE a.id = :id")
    fun load(id: Long?): Application?

    @Query("SELECT * FROM Application a")
    fun loadAll(): List<Application>

    @Query("SELECT * FROM Application a")
    fun loadAllAsCursor(): Cursor

    @Update
    fun update(application: Application)
}
