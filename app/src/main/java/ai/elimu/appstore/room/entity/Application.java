package ai.elimu.appstore.room.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import java.util.Set;

import ai.elimu.model.enums.admin.ApplicationStatus;
import ai.elimu.model.enums.content.LiteracySkill;
import ai.elimu.model.enums.content.NumeracySkill;

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

    private Set<LiteracySkill> literacySkills;

    private Set<NumeracySkill> numeracySkills;

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

    public Set<LiteracySkill> getLiteracySkills() {
        return literacySkills;
    }

    public void setLiteracySkills(Set<LiteracySkill> literacySkills) {
        this.literacySkills = literacySkills;
    }

    public Set<NumeracySkill> getNumeracySkills() {
        return numeracySkills;
    }

    public void setNumeracySkills(Set<NumeracySkill> numeracySkills) {
        this.numeracySkills = numeracySkills;
    }
}
