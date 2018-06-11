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

    private Long appGroupId;
    @ToOne(joinProperty = "appGroupId")
    private AppGroup appGroup;

    /**
     * Keeps track of the application's position in the list, as received in the JSON response.
     */
//    @NotNull
    private Integer listOrder;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 843119501)
    private transient ApplicationDao myDao;

    @Generated(hash = 1701894269)
    public Application(Long id, @NotNull Locale locale, @NotNull String packageName,
            Boolean infrastructural, Set<LiteracySkill> literacySkills,
            Set<NumeracySkill> numeracySkills, @NotNull ApplicationStatus applicationStatus,
            Long appGroupId, Integer listOrder) {
        this.id = id;
        this.locale = locale;
        this.packageName = packageName;
        this.infrastructural = infrastructural;
        this.literacySkills = literacySkills;
        this.numeracySkills = numeracySkills;
        this.applicationStatus = applicationStatus;
        this.appGroupId = appGroupId;
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

    public Long getAppGroupId() {
        return this.appGroupId;
    }

    public void setAppGroupId(Long appGroupId) {
        this.appGroupId = appGroupId;
    }

    public Integer getListOrder() {
        return this.listOrder;
    }

    public void setListOrder(Integer listOrder) {
        this.listOrder = listOrder;
    }

    @Generated(hash = 759378586)
    private transient Long appGroup__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1728555289)
    public AppGroup getAppGroup() {
        Long __key = this.appGroupId;
        if (appGroup__resolvedKey == null || !appGroup__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            AppGroupDao targetDao = daoSession.getAppGroupDao();
            AppGroup appGroupNew = targetDao.load(__key);
            synchronized (this) {
                appGroup = appGroupNew;
                appGroup__resolvedKey = __key;
            }
        }
        return appGroup;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1753195535)
    public void setAppGroup(AppGroup appGroup) {
        synchronized (this) {
            this.appGroup = appGroup;
            appGroupId = appGroup == null ? null : appGroup.getId();
            appGroup__resolvedKey = appGroupId;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1259587109)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getApplicationDao() : null;
    }
}
