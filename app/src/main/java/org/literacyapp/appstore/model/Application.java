package org.literacyapp.appstore.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

@Entity
public class Application {

    @Id
    private Long id;

//    @NotNull
//    private Locale locale;

    @NotNull
    private String packageName;

//    literacySkills
//
//    numeracySkills
//
//    applicationStatus

    @Generated(hash = 1213546295)
    public Application(Long id, @NotNull String packageName) {
        this.id = id;
        this.packageName = packageName;
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

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
