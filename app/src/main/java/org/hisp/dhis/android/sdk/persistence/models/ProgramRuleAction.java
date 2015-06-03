package org.hisp.dhis.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;

import java.util.Map;

/**
 * @author Simen Skogly Russnes on 29.04.15.
 */
@Table(databaseName = Dhis2Database.NAME)
public class ProgramRuleAction extends BaseIdentifiableObject {

    public static final String TYPE_HIDEFIELD = "HIDEFIELD";
    public static final String TYPE_HIDESECTION = "HIDESECTION";

    @Column
    private String programRuleActionType;

    @Column
    private boolean externalAccess;

    @Column
    private String displayName;

    @Column
    protected String programRule;

    @JsonProperty("programRule")
    public void setProgramRule(Map<String, Object> programRule) {
        this.programRule = (String) programRule.get("id");
    }

    @Column
    protected String dataElement;

    @JsonProperty("dataElement")
    public void setDataElement(Map<String, Object> dataElement) {
        this.dataElement = (String) dataElement.get("id");
    }

    @Column
    protected String programStageSection;

    @JsonProperty("programStageSection")
    public void setProgramStageSection(Map<String, Object> programStageSection) {
        this.programStageSection = (String) programStageSection.get("id");
    }

    public String getProgramStageSection() {
        return programStageSection;
    }

    public String getDataElement() {
        return dataElement;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getProgramRule() {
        return programRule;
    }

    public boolean getExternalAccess() {
        return externalAccess;
    }

    public String getProgramRuleActionType() {
        return programRuleActionType;
    }

    public void setProgramRuleActionType(String programRuleActionType) {
        this.programRuleActionType = programRuleActionType;
    }

    public boolean isExternalAccess() {
        return externalAccess;
    }

    public void setExternalAccess(boolean externalAccess) {
        this.externalAccess = externalAccess;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setProgramRule(String programRule) {
        this.programRule = programRule;
    }

    public void setDataElement(String dataElement) {
        this.dataElement = dataElement;
    }

    public void setProgramStageSection(String programStageSection) {
        this.programStageSection = programStageSection;
    }
}
