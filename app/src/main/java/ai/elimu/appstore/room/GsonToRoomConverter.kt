package ai.elimu.appstore.room

import ai.elimu.appstore.room.entity.Application
import ai.elimu.appstore.room.entity.ApplicationVersion
import ai.elimu.model.v2.gson.application.ApplicationGson
import ai.elimu.model.v2.gson.application.ApplicationVersionGson

object GsonToRoomConverter {
    fun getApplication(applicationGson: ApplicationGson?): Application? {
        if (applicationGson == null) {
            return null
        } else {
            val application = Application()

            // BaseEntity
            application.id = applicationGson.id

            // Application
            application.packageName = applicationGson.packageName
            application.infrastructural = applicationGson.infrastructural
            application.applicationStatus = applicationGson.applicationStatus
            application.literacySkills = applicationGson.literacySkills
            application.numeracySkills = applicationGson.numeracySkills

            return application
        }
    }

    fun getApplicationVersion(
        applicationGson: ApplicationGson,
        applicationVersionGson: ApplicationVersionGson?
    ): ApplicationVersion? {
        if (applicationVersionGson == null) {
            return null
        } else {
            val applicationVersion = ApplicationVersion()

            // BaseEntity
            applicationVersion.id = applicationVersionGson.id

            // ApplicationVersion
            applicationVersion.applicationId = applicationGson.id
            applicationVersion.fileUrl = applicationVersionGson.fileUrl
            applicationVersion.fileSizeInKb = applicationVersionGson.fileSizeInKb
            applicationVersion.checksumMd5 = applicationVersionGson.checksumMd5
            applicationVersion.versionCode = applicationVersionGson.versionCode

            return applicationVersion
        }
    }
}
