package org.literacyapp.appstore.model;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.literacyapp.appstore.dao.converter.ApplicationStatusConverter;
import org.literacyapp.appstore.dao.converter.LiteracySkillSetConverter;
import org.literacyapp.appstore.dao.converter.LocaleConverter;
import org.literacyapp.appstore.dao.converter.NumeracySkillSetConverter;
import org.literacyapp.model.enums.Locale;
import org.literacyapp.model.enums.admin.ApplicationStatus;
import org.literacyapp.model.enums.content.LiteracySkill;
import org.literacyapp.model.enums.content.NumeracySkill;

import java.util.Set;

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
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
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

    public ApplicationStatus getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(ApplicationStatus applicationStatus) {
        this.applicationStatus = applicationStatus;
    }
}
