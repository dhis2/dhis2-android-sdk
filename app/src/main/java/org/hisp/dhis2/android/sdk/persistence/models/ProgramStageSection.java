package org.hisp.dhis2.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis2.android.sdk.controllers.metadata.MetaDataController;

import java.util.List;
import java.util.Map;

/**
 * @author Simen Skogly Russnes on 26.03.15.
 */
@Table
public class ProgramStageSection extends BaseIdentifiableObject {

    @JsonProperty("sortOrder")
    @Column
    public int sortOrder;

    @JsonProperty("externalAccess")
    @Column
    public boolean externalAccess;

    @JsonProperty("displayName")
    @Column
    public String displayName;

    @JsonProperty("programStage")
    public void setProgramStage(Map<String, Object> programStage) {
        this.programStage = (String) programStage.get("id");
    }

    @Column
    public String programStage;

    @JsonProperty("programStageDataElements")
    private List<ProgramStageDataElement> programStageDataElements;

    public List<ProgramStageDataElement> getProgramStageDataElements() {
        if(programStageDataElements == null)
            programStageDataElements = Select.all(ProgramStageDataElement.class, Condition.column(ProgramStageDataElement$Table.PROGRAMSTAGESECTION).is(id));
        return programStageDataElements;
    }

    @JsonProperty("programIndicators")
    private List<ProgramIndicator> programIndicators;

    public List<ProgramIndicator> getProgramIndicators() {
        if(programIndicators == null)
            programIndicators = MetaDataController.getProgramIndicatorsBySection(id);
        return programIndicators;
    }


}
