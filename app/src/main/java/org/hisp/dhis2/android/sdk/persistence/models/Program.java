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
public class Program extends BaseIdentifiableObject {

    @JsonProperty("type")
    @Column
    public int type;

    @JsonProperty("kind")
    @Column
    public String kind;

    @JsonProperty("version")
    @Column
    public int version;

    @JsonProperty("dateOfEnrollmentDescription")
    @Column
    public String dateOfEnrollmentDescription;

    @JsonProperty("description")
    @Column
    public String description;

    @JsonProperty("onlyEnrollOnce")
    @Column
    public boolean onlyEnrollOnce;

    @JsonProperty("externalAccess")
    @Column
    public boolean extenalAccess;

    @JsonProperty("displayIncidentDate")
    @Column
    public boolean displayIncidentDate;

    @JsonProperty("dateOfIncidentDateDescription")
    @Column
    public boolean dateOfIncidentDateDescription;

    @JsonProperty("registration")
    @Column
    public boolean registration;

    @JsonProperty("selectEnrollmentDatesInFuture")
    @Column
    public boolean selectEnrollmentDatesInFuture;

    @JsonProperty("dataEntryMethod")
    @Column
    public boolean dataEntryMethod;

    @JsonProperty("singleEvent")
    @Column
    public boolean singleEvent;

    @JsonProperty("ignoreOverdueEvents")
    @Column
    public boolean ignoreOverdueEvents;

    @JsonProperty("relationshipFromA")
    @Column
    public boolean relationshipFromA;

    @JsonProperty("displayName")
    @Column
    public String displayName;

    @JsonProperty("selectIncidentDatesInFuture")
    @Column
    public boolean selectIncidentDatesInFuture;

    @JsonProperty("trackedEntity")
    @Column(columnType = Column.FOREIGN_KEY,
            references = {@ForeignKeyReference(columnName = "trackedEntity",
            columnType = String.class, foreignColumnName = "id")})
    public TrackedEntity trackedEntity;

    //@JsonProperty("programStages")
    private List<ProgramStage> programStages;

    @JsonProperty("programTrackedEntityAttributes")
    private List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes;

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
                    Condition.column(ProgramStage$Table.PROGRAM).is(id));
        }
        return programStages;
    }

    public List<ProgramTrackedEntityAttribute> getProgramTrackedEntityAttributes() {
        if(programTrackedEntityAttributes == null) {
            programTrackedEntityAttributes = Select.all(ProgramTrackedEntityAttribute.class,
                    Condition.column(ProgramTrackedEntityAttribute$Table.PROGRAM).is(id));
        }
        return programTrackedEntityAttributes;
    }


}
