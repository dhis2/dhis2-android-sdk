package org.hisp.dhis.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.persistence.Dhis2Database;

import java.util.List;
import java.util.Map;

/**
 * @author Simen Skogly Russnes on 26.03.15.
 */
@Table(databaseName = Dhis2Database.NAME)
public class ProgramStageSection extends BaseIdentifiableObject {

    @JsonProperty("sortOrder")
    @Column(name = "sortOrder")
    int sortOrder;

    @JsonProperty("externalAccess")
    @Column(name = "externalAccess")
    boolean externalAccess;

    @JsonProperty("displayName")
    @Column(name = "displayName")
    String displayName;

    @Column(name = "programStage")
    String programStage;

    @JsonProperty("programStageDataElements")
    List<ProgramStageDataElement> programStageDataElements;

    @JsonProperty("programIndicators")
    List<ProgramIndicator> programIndicators;

    @JsonProperty("programStage")
    public void setProgramStage(Map<String, Object> programStage) {
        this.programStage = (String) programStage.get("id");
    }

    public List<ProgramStageDataElement> getProgramStageDataElements() {
        if (programStageDataElements == null) {
            programStageDataElements = MetaDataController.getProgramStageDataElements(this);
        }

        return programStageDataElements;
    }

    public void setProgramStageDataElements(List<ProgramStageDataElement> programStageDataElements) {
        this.programStageDataElements = programStageDataElements;
    }

    public List<ProgramIndicator> getProgramIndicators() {
        if (programIndicators == null)
            programIndicators = MetaDataController.getProgramIndicatorsBySection(id);
        return programIndicators;
    }

    public void setProgramIndicators(List<ProgramIndicator> programIndicators) {
        this.programIndicators = programIndicators;
    }

    public String getProgramStage() {
        return programStage;
    }

    public void setProgramStage(String programStage) {
        this.programStage = programStage;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean getExternalAccess() {
        return externalAccess;
    }

    public void setExternalAccess(boolean externalAccess) {
        this.externalAccess = externalAccess;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
