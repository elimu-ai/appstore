package ai.elimu.appstore.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.DaoException;
import ai.elimu.appstore.dao.DaoSession;
import ai.elimu.appstore.dao.AppCategoryDao;
import ai.elimu.appstore.dao.AppGroupDao;

@Entity
public class AppGroup {

    @Id
    private Long id;

    private Long appCategoryId;
    @ToOne(joinProperty = "appCategoryId")
    private AppCategory appCategory;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 454501991)
    private transient AppGroupDao myDao;

    @Generated(hash = 1404519891)
    private transient Long appCategory__resolvedKey;

    @Generated(hash = 1254552308)
    public AppGroup(Long id, Long appCategoryId) {
        this.id = id;
        this.appCategoryId = appCategoryId;
    }

    @Generated(hash = 1988496527)
    public AppGroup() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAppCategoryId() {
        return this.appCategoryId;
    }

    public void setAppCategoryId(Long appCategoryId) {
        this.appCategoryId = appCategoryId;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 676421978)
    public AppCategory getAppCategory() {
        Long __key = this.appCategoryId;
        if (appCategory__resolvedKey == null
                || !appCategory__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            AppCategoryDao targetDao = daoSession.getAppCategoryDao();
            AppCategory appCategoryNew = targetDao.load(__key);
            synchronized (this) {
                appCategory = appCategoryNew;
                appCategory__resolvedKey = __key;
            }
        }
        return appCategory;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 485792712)
    public void setAppCategory(AppCategory appCategory) {
        synchronized (this) {
            this.appCategory = appCategory;
            appCategoryId = appCategory == null ? null : appCategory.getId();
            appCategory__resolvedKey = appCategoryId;
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
    @Generated(hash = 1614251138)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getAppGroupDao() : null;
    }
}
