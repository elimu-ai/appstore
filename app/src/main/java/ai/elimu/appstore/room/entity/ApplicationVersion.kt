package ai.elimu.appstore.room.entity

import androidx.room.Entity

/**
 * For documentation, see [model](https://github.com/elimu-ai/webapp/tree/master/src/main/java/ai/elimu/model)
 */
@Entity
class ApplicationVersion : BaseEntity() {
    @JvmField
    var applicationId: Long = 0

    @JvmField
    var fileUrl: String = ""

    @JvmField
    var fileSizeInKb: Int = 0

    @JvmField
    var checksumMd5: String = ""

    @JvmField
    var versionCode: Int = 0
}
