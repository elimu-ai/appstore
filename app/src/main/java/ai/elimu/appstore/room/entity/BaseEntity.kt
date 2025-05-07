package ai.elimu.appstore.room.entity

import androidx.room.PrimaryKey

/**
 * For documentation, see [model](https://github.com/elimu-ai/webapp/tree/master/src/main/java/ai/elimu/model)
 */
open class BaseEntity {
    /**
     * Reflects the ID stored in the backend webapp's database. Therefore, `@PrimaryKey(autoGenerate = true)` is
     * not used.
     */
    @JvmField
    @PrimaryKey
    var id: Long? = null
}
