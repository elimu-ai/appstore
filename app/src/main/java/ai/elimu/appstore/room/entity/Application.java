package ai.elimu.appstore.room.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import ai.elimu.model.enums.admin.ApplicationStatus;

/**
 * For documentation, see https://github.com/elimu-ai/webapp/tree/master/src/main/java/ai/elimu/model
 */
@Entity
public class Application extends BaseEntity {

    @NonNull
    private String packageName;

    private Boolean infrastructural;

    @NonNull
    private ApplicationStatus applicationStatus;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Boolean getInfrastructural() {
        return infrastructural;
    }

    public void setInfrastructural(Boolean infrastructural) {
        this.infrastructural = infrastructural;
    }

    public ApplicationStatus getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(ApplicationStatus applicationStatus) {
        this.applicationStatus = applicationStatus;
    }
}
