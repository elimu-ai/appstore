package ai.elimu.appstore.room

import ai.elimu.model.v2.enums.admin.ApplicationStatus
import ai.elimu.model.v2.enums.content.LiteracySkill
import ai.elimu.model.v2.enums.content.NumeracySkill
import android.text.TextUtils
import androidx.room.TypeConverter
import org.json.JSONArray
import org.json.JSONException
import timber.log.Timber

/**
 * See https://developer.android.com/training/data-storage/room/referencing-data
 */
object EnumConverter {
    @JvmStatic
    @TypeConverter
    fun fromApplicationStatus(value: String?): ApplicationStatus? {
        var applicationStatus: ApplicationStatus? = null
        if (!TextUtils.isEmpty(value)) {
            applicationStatus = ApplicationStatus.valueOf(value!!)
        }
        return applicationStatus
    }

    @JvmStatic
    @TypeConverter
    fun toApplicationStatus(applicationStatus: ApplicationStatus?): String? {
        var value: String? = null
        if (applicationStatus != null) {
            value = applicationStatus.toString()
        }
        return value
    }

    @JvmStatic
    @TypeConverter
    fun fromLiteracySkills(value: String?): Set<LiteracySkill> {
        val literacySkills: MutableSet<LiteracySkill> = HashSet()
        if (!TextUtils.isEmpty(value)) {
            try {
                val jsonArray = JSONArray(value)
                Timber.d("jsonArray: $jsonArray")
                for (i in 0..<jsonArray.length()) {
                    val literacySkillAsString = jsonArray.getString(i)
                    Timber.d("literacySkillAsString: $literacySkillAsString")
                    val literacySkill = LiteracySkill.valueOf(literacySkillAsString)
                    literacySkills.add(literacySkill)
                }
            } catch (e: JSONException) {
                Timber.e(e)
            }
        }
        return literacySkills
    }

    @JvmStatic
    @TypeConverter
    fun toLiteracySkills(literacySkills: Set<LiteracySkill?>?): String? {
        var value: String? = null
        if (literacySkills != null) {
            value = literacySkills.toString()
        }
        Timber.i("value: $value")
        return value
    }

    @JvmStatic
    @TypeConverter
    fun fromNumeracySkills(value: String?): Set<NumeracySkill> {
        val numeracySkills: MutableSet<NumeracySkill> = HashSet()
        if (!TextUtils.isEmpty(value)) {
            try {
                val jsonArray = JSONArray(value)
                Timber.d("jsonArray: $jsonArray")
                for (i in 0..<jsonArray.length()) {
                    val numeracySkillAsString = jsonArray.getString(i)
                    Timber.d("numeracySkillAsString: $numeracySkillAsString")
                    val numeracySkill = NumeracySkill.valueOf(numeracySkillAsString)
                    numeracySkills.add(numeracySkill)
                }
            } catch (e: JSONException) {
                Timber.e(e)
            }
        }
        return numeracySkills
    }

    @JvmStatic
    @TypeConverter
    fun toNumeracySkills(numeracySkills: Set<NumeracySkill?>?): String? {
        var value: String? = null
        if (numeracySkills != null) {
            value = numeracySkills.toString()
        }
        Timber.i("value: $value")
        return value
    }
}
