package ai.elimu.appstore.model;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.Set;

import ai.elimu.appstore.dao.converter.ApplicationStatusConverter;
import ai.elimu.appstore.dao.converter.LiteracySkillSetConverter;
import ai.elimu.appstore.dao.converter.LocaleConverter;
import ai.elimu.appstore.dao.converter.NumeracySkillSetConverter;
import ai.elimu.model.enums.Locale;
import ai.elimu.model.enums.admin.ApplicationStatus;
import ai.elimu.model.enums.content.LiteracySkill;
import ai.elimu.model.enums.content.NumeracySkill;
import org.greenrobot.greendao.DaoException;
import ai.elimu.appstore.dao.DaoSession;
import ai.elimu.appstore.dao.AppGroupDao;
import ai.elimu.appstore.dao.ApplicationDao;

@Entity
public class Application {

    @Id
    private Long id;

    @NotNull
    @Convert(converter = LocaleConverter.class, columnType = String.class)
    private Locale locale;

    @NotNull
    private String packageName;

    private Boolean infrastructural;

    @Convert(converter = LiteracySkillSetConverter.class, columnType = String.class)
    private Set<LiteracySkill> literacySkills;

    @Convert(converter = NumeracySkillSetConverter.class, columnType = String.class)
    private Set<NumeracySkill> numeracySkills;

    @NotNull
    @Convert(converter = ApplicationStatusConverter.class, columnType = String.class)
    private ApplicationStatus applicationStatus;

//    /**
//     * AppCategory name. This property is only set if the Application belongs to a Custom Project.
//     */
//    private Long appGroupId;
//    @ToOne(joinProperty = "appGroupId")
//    private AppGroup appGroup;

    /**
     * AppCategory name. This property is only set if the Application belongs to a Custom Project.
     */
    @Deprecated // TODO: replace with usage of AppGroup
    private String name;

    /**
     * AppCategory backgroundColor. This property is only set if the Application belongs to a Custom Project.
     */
    @Deprecated // TODO: replace with usage of AppGroup
    private String backgroundColor;

    /**
     * Keeps track of the application's position in the list, as received in the JSON response.
     */
//    @NotNull
    private Integer listOrder;

    @Generated(hash = 1342442865)
    public Application(Long id, @NotNull Locale locale, @NotNull String packageName, Boolean infrastructural,
            Set<LiteracySkill> literacySkills, Set<NumeracySkill> numeracySkills,
            @NotNull ApplicationStatus applicationStatus, String name, String backgroundColor,
            Integer listOrder) {
        this.id = id;
        this.locale = locale;
        this.packageName = packageName;
        this.infrastructural = infrastructural;
        this.literacySkills = literacySkills;
        this.numeracySkills = numeracySkills;
        this.applicationStatus = applicationStatus;
        this.name = name;
        this.backgroundColor = backgroundColor;
        this.listOrder = listOrder;
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

    public Boolean getInfrastructural() {
        return this.infrastructural;
    }

    public void setInfrastructural(Boolean infrastructural) {
        this.infrastructural = infrastructural;
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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBackgroundColor() {
        return this.backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Integer getListOrder() {
        return this.listOrder;
    }

    public void setListOrder(Integer listOrder) {
        this.listOrder = listOrder;
    }
}
