package ai.elimu.appstore.model.project;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class AppCategory {

    @Id
    private Long id;

    @NotNull
    private String name;

    private String backgroundColor;

    @Generated(hash = 660658780)
    public AppCategory(Long id, @NotNull String name, String backgroundColor) {
        this.id = id;
        this.name = name;
        this.backgroundColor = backgroundColor;
    }

    @Generated(hash = 469938490)
    public AppCategory() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
