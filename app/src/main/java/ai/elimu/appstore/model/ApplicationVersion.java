package ai.elimu.appstore.model;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.Calendar;

import ai.elimu.appstore.dao.ApplicationDao;
import ai.elimu.appstore.dao.ApplicationVersionDao;
import ai.elimu.appstore.dao.DaoSession;
import ai.elimu.appstore.dao.converter.CalendarConverter;

@Entity
public class ApplicationVersion {

    @Id
    private Long id;

    private long applicationId;
    @ToOne(joinProperty = "applicationId")
    private Application application;

    @NotNull
    private Integer fileSizeInKb;

    @NotNull
    private String fileUrl;

    @NotNull
    private String checksumMd5;

    @NotNull
    private String contentType;

    @NotNull
    private Integer versionCode;

    private String startCommand;

    @NotNull
    @Convert(converter = CalendarConverter.class, columnType = Long.class)
    private Calendar timeUploaded;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1339503542)
    private transient ApplicationVersionDao myDao;

    @Generated(hash = 597723545)
    public ApplicationVersion(Long id, long applicationId,
            @NotNull Integer fileSizeInKb, @NotNull String fileUrl,
            @NotNull String checksumMd5, @NotNull String contentType,
            @NotNull Integer versionCode, String startCommand,
            @NotNull Calendar timeUploaded) {
        this.id = id;
        this.applicationId = applicationId;
        this.fileSizeInKb = fileSizeInKb;
        this.fileUrl = fileUrl;
        this.checksumMd5 = checksumMd5;
        this.contentType = contentType;
        this.versionCode = versionCode;
        this.startCommand = startCommand;
        this.timeUploaded = timeUploaded;
    }

    @Generated(hash = 386036356)
    public ApplicationVersion() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getApplicationId() {
        return this.applicationId;
    }

    public void setApplicationId(long applicationId) {
        this.applicationId = applicationId;
    }

    public Integer getFileSizeInKb() {
        return this.fileSizeInKb;
    }

    public void setFileSizeInKb(Integer fileSizeInKb) {
        this.fileSizeInKb = fileSizeInKb;
    }

    public String getFileUrl() {
        return this.fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getChecksumMd5() {
        return this.checksumMd5;
    }

    public void setChecksumMd5(String checksumMd5) {
        this.checksumMd5 = checksumMd5;
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Integer getVersionCode() {
        return this.versionCode;
    }

    public void setVersionCode(Integer versionCode) {
        this.versionCode = versionCode;
    }

    public String getStartCommand() {
        return this.startCommand;
    }

    public void setStartCommand(String startCommand) {
        this.startCommand = startCommand;
    }

    public Calendar getTimeUploaded() {
        return this.timeUploaded;
    }

    public void setTimeUploaded(Calendar timeUploaded) {
        this.timeUploaded = timeUploaded;
    }

    @Generated(hash = 110579603)
    private transient Long application__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 2086637588)
    public Application getApplication() {
        long __key = this.applicationId;
        if (application__resolvedKey == null
                || !application__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ApplicationDao targetDao = daoSession.getApplicationDao();
            Application applicationNew = targetDao.load(__key);
            synchronized (this) {
                application = applicationNew;
                application__resolvedKey = __key;
            }
        }
        return application;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 494462575)
    public void setApplication(@NotNull Application application) {
        if (application == null) {
            throw new DaoException(
                    "To-one property 'applicationId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.application = application;
            applicationId = application.getId();
            application__resolvedKey = applicationId;
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
    @Generated(hash = 1698784280)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getApplicationVersionDao() : null;
    }
}
