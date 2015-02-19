package org.hisp.dhis2.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis2.android.sdk.persistence.Dhis2Database;

import java.util.List;

/**
 * @author Simen Skogly Russnes on 17.02.15.
 */
@Table(databaseName = Dhis2Database.NAME)
public class Program extends BaseDataModel {

    @JsonProperty("trackedEntity")
    @Column(columnType = Column.FOREIGN_KEY,
            references = {@ForeignKeyReference(columnName = "trackedEntity",
            columnType = String.class, foreignColumnName = "id")})
    public TrackedEntity trackedEntity;

    @JsonProperty("programStages")
    private List<ProgramStage> programStages;

    @JsonAnySetter
    public void handleUnknown(String key, Object value) {
        // do something: put to a Map; log a warning, whatever
    }

    public Program() {}

    public TrackedEntity getTrackedEntity() {
        return trackedEntity;
    }

    public void setTrackedEntity(TrackedEntity trackedEntity) {
        this.trackedEntity = trackedEntity;
    }

    /**
     * Using lazy loading for one to many relationships
     * @return
     */
    public List<ProgramStage> getProgramStages() {
        if(programStages == null) {
            programStages = Select.all(ProgramStage.class,
                    Condition.column(ProgramStage$Table.PROGRAM_PROGRAM).is(id));
        }
        return programStages;
    }
}
