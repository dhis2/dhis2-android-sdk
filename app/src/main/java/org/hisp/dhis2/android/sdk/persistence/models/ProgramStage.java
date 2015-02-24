package org.hisp.dhis2.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis2.android.sdk.persistence.Dhis2Database;

import java.util.List;
import java.util.Map;

/**
 * @author Simen Skogly Russnes on 17.02.15.
 */
@Table(databaseName = Dhis2Database.NAME)
public class ProgramStage extends BaseIdentifiableObject {

    @JsonProperty("program")
    public void setProgram(Map<String, Object> program) {
        this.program = (String) program.get("id");
    }

    @Column
    public String program;

    @JsonProperty("dataEntryType")
    @Column
    public String dataEntryType;

    @JsonProperty("blockEntryForm")
    @Column
    public boolean blockEntryForm;

    @JsonProperty("reportDateDescription")
    @Column
    public String reportDateDescription;

    @JsonProperty("displayGenerateEventBox")
    @Column
    public boolean displayGenerateEventBox;

    @JsonProperty("description")
    @Column
    public String description;

    @JsonProperty("externalAccess")
    @Column
    public boolean externalAccess;

    @JsonProperty("openAfterEnrollment")
    @Column
    public boolean openAfterEnrollment;

    @JsonProperty("captureCoordinates")
    @Column
    public boolean captureCoordinates;

    @JsonProperty("defaultTemplateMessage")
    @Column
    public String defaultTemplateMessage;

    @JsonProperty("remindCompleted")
    @Column
    public boolean remindCompleted;

    @JsonProperty("validCompleteOnly")
    @Column
    public boolean validCompleteOnly;

    @JsonProperty("sortOrder")
    @Column
    public int sortOrder;

    @JsonProperty("generatedByEnrollmentDate")
    @Column
    public boolean generatedByEnrollmentDate;

    @JsonProperty("preGenerateUID")
    @Column
    public boolean preGenerateUID;

    @JsonProperty("autoGenerateEvent")
    @Column
    public boolean autoGenerateEvent;

    @JsonProperty("allowGenerateNextVisit")
    @Column
    public boolean allowGenerateNextVisit;

    @JsonProperty("repeatable")
    @Column
    public boolean repeatable;

    @JsonProperty("minDaysFromStart")
    @Column
    public int minDaysFromStart;

    @JsonProperty("displayName")
    @Column
    public String displayName;

    @JsonProperty("programStageDataElements")
    private List<ProgramStageDataElement> programStageDataElements;

    public List<ProgramStageDataElement> getProgramStageDataElements() {
        if(programStageDataElements == null) {
            programStageDataElements = Select.all(ProgramStageDataElement.class,
                    Condition.column(ProgramStageDataElement$Table.PROGRAMSTAGE).is(id));
        }
        return programStageDataElements;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }
}
