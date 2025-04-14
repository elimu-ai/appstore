package ai.elimu.appstore.room.entity

import ai.elimu.model.v2.enums.admin.ApplicationStatus
import ai.elimu.model.v2.enums.content.LiteracySkill
import ai.elimu.model.v2.enums.content.NumeracySkill
import androidx.room.Entity

/**
 * For documentation, see https://github.com/elimu-ai/webapp/tree/master/src/main/java/ai/elimu/model
 */
@Entity
class Application : BaseEntity() {
    lateinit var packageName: String

    @JvmField
    var infrastructural: Boolean? = null

    lateinit var applicationStatus: ApplicationStatus

    @JvmField
    var literacySkills: Set<LiteracySkill>? = null

    @JvmField
    var numeracySkills: Set<NumeracySkill>? = null
}
