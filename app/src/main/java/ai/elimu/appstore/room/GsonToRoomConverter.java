package ai.elimu.appstore.room;

import ai.elimu.appstore.room.entity.Application;
import ai.elimu.model.v2.gson.application.ApplicationGson;

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
            application.setInfrastructural(applicationGson.isInfrastructural());
            application.setApplicationStatus(applicationGson.getApplicationStatus());

            return application;
        }
    }
}
