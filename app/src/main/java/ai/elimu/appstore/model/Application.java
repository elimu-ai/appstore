package ai.elimu.appstore.model;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

import java.util.Set;

import ai.elimu.appstore.dao.converter.ApplicationStatusConverter;
import ai.elimu.appstore.dao.converter.LiteracySkillSetConverter;
import ai.elimu.appstore.dao.converter.LocaleConverter;
import ai.elimu.appstore.dao.converter.NumeracySkillSetConverter;
import ai.elimu.model.enums.Locale;
import ai.elimu.model.enums.admin.ApplicationStatus;
import ai.elimu.model.enums.content.LiteracySkill;
import ai.elimu.model.enums.content.NumeracySkill;

@Entity
public class Application {

    @Id
    private Long id;

    @NotNull
    @Convert(converter = LocaleConverter.class, columnType = String.class)
    private Locale locale;

    @NotNull
    private String packageName;

    @Convert(converter = LiteracySkillSetConverter.class, columnType = String.class)
    private Set<LiteracySkill> literacySkills;

    @Convert(converter = NumeracySkillSetConverter.class, columnType = String.class)
    private Set<NumeracySkill> numeracySkills;

    @NotNull
    @Convert(converter = ApplicationStatusConverter.class, columnType = String.class)
    private ApplicationStatus applicationStatus;

    @Generated(hash = 2022782533)
    public Application(Long id, @NotNull Locale locale, @NotNull String packageName,
            Set<LiteracySkill> literacySkills, Set<NumeracySkill> numeracySkills,
            @NotNull ApplicationStatus applicationStatus) {
        this.id = id;
        this.locale = locale;
        this.packageName = packageName;
        this.literacySkills = literacySkills;
        this.numeracySkills = numeracySkills;
        this.applicationStatus = applicationStatus;
    }

    @Generated(hash = 312658882)
    public Application() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Set<LiteracySkill> getLiteracySkills() {
        return this.literacySkills;
    }

    public void setLiteracySkills(Set<LiteracySkill> literacySkills) {
        this.literacySkills = literacySkills;
    }

    public Set<NumeracySkill> getNumeracySkills() {
        return this.numeracySkills;
    }

    public void setNumeracySkills(Set<NumeracySkill> numeracySkills) {
        this.numeracySkills = numeracySkills;
    }

    public ApplicationStatus getApplicationStatus() {
        return this.applicationStatus;
    }

    public void setApplicationStatus(ApplicationStatus applicationStatus) {
        this.applicationStatus = applicationStatus;
    }
}
