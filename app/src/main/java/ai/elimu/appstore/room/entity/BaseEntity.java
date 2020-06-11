package ai.elimu.appstore.room.entity;

import androidx.room.PrimaryKey;

/**
 * For documentation, see https://github.com/elimu-ai/webapp/tree/master/src/main/java/ai/elimu/model
 */
public class BaseEntity {

    /**
     * Reflects the ID stored in the backend webapp's database. Therefore, {@code @PrimaryKey(autoGenerate = true)} is
     * not used.
     */
    @PrimaryKey
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
