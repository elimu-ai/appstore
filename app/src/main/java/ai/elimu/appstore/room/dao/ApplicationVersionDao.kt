package ai.elimu.appstore.room.dao

import ai.elimu.appstore.room.entity.ApplicationVersion
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ApplicationVersionDao {
    @Insert
    fun insert(applicationVersion: ApplicationVersion)

    @Query("SELECT * FROM ApplicationVersion av WHERE av.id = :id")
    fun load(id: Long?): ApplicationVersion?

    @Query("SELECT * FROM ApplicationVersion av ORDER BY av.applicationId ASC, av.versionCode DESC")
    fun loadAll(): List<ApplicationVersion>

    @Query("SELECT * FROM ApplicationVersion av WHERE av.applicationId = :applicationId ORDER BY versionCode DESC")
    fun loadAll(applicationId: Long?): List<ApplicationVersion>

    @Update
    fun update(applicationVersion: ApplicationVersion)

    @Query("DELETE FROM ApplicationVersion WHERE applicationId = :applicationId")
    fun delete(applicationId: Long)
}
