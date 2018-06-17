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

    /**
     * Keeps track of the AppCategory's position in the list, as received in the JSON response.
     *
     * TODO: add listOrder to backend
     */
//    @NotNull
    private Integer listOrder;

    @Generated(hash = 1558937943)
    public AppCategory(Long id, @NotNull String name, String backgroundColor, Integer listOrder) {
        this.id = id;
        this.name = name;
        this.backgroundColor = backgroundColor;
        this.listOrder = listOrder;
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

    public Integer getListOrder() {
        return this.listOrder;
    }

    public void setListOrder(Integer listOrder) {
        this.listOrder = listOrder;
    }
}
