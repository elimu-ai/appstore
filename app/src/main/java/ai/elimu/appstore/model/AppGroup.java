package ai.elimu.appstore.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class AppGroup {

    @Id
    private Long id;

    @Generated(hash = 1791354887)
    public AppGroup(Long id) {
        this.id = id;
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
}
