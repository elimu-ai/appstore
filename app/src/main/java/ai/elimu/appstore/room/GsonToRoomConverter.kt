package ai.elimu.appstore.room;

import ai.elimu.appstore.room.entity.Application;
import ai.elimu.appstore.room.entity.ApplicationVersion;
import ai.elimu.model.v2.gson.application.ApplicationGson;
import ai.elimu.model.v2.gson.application.ApplicationVersionGson;

public class GsonToRoomConverter {

    public static Application getApplication(ApplicationGson applicationGson) {
        if (applicationGson == null) {
            return null;
        } else {
            Application application = new Application();

            // BaseEntity
            application.setId(applicationGson.getId());

            // Application
            application.setPackageName(applicationGson.getPackageName());
            application.setInfrastructural(applicationGson.getInfrastructural());
            application.setApplicationStatus(applicationGson.getApplicationStatus());
            application.setLiteracySkills(applicationGson.getLiteracySkills());
            application.setNumeracySkills(applicationGson.getNumeracySkills());

            return application;
        }
    }

    public static ApplicationVersion getApplicationVersion(ApplicationGson applicationGson, ApplicationVersionGson applicationVersionGson) {
        if (applicationVersionGson == null) {
            return null;
        } else {
            ApplicationVersion applicationVersion = new ApplicationVersion();

            // BaseEntity
            applicationVersion.setId(applicationVersionGson.getId());

            // ApplicationVersion
            applicationVersion.setApplicationId(applicationGson.getId());
            applicationVersion.setFileUrl(applicationVersionGson.getFileUrl());
            applicationVersion.setFileSizeInKb(applicationVersionGson.getFileSizeInKb());
            applicationVersion.setChecksumMd5(applicationVersionGson.getChecksumMd5());
            applicationVersion.setVersionCode(applicationVersionGson.getVersionCode());

            return applicationVersion;
        }
    }
}
