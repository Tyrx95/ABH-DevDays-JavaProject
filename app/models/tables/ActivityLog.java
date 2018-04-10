package models.tables;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * The type Activity log
 */
@Entity
@Table(name = "activity_log")
public class ActivityLog {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @Basic
    private Timestamp logTime;

    @Column(name = "description")
    private String description;

    public ActivityLog(Timestamp logTime, String description) {
        this.logTime = logTime;
        this.description = description;
    }

    public ActivityLog() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Timestamp getLogTime() {
        return logTime;
    }

    public ActivityLog setLogTime(Timestamp logTime) {
        this.logTime = logTime;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ActivityLog setDescription(String description) {
        this.description = description;
        return this;
    }
}
