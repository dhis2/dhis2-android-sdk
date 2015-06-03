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
public class ProgramRuleVariable extends BaseIdentifiableObject {

    @JsonProperty("programRuleVariableSourceType")
    @Column
    private String sourceType;

    @Column
    private boolean externalAccess;

    @Column
    private String displayName;

    @Column
    protected String program;

    @JsonProperty("program")
    public void setProgram(Map<String, Object> program) {
        this.program = (String) program.get("id");
    }

    @Column
    protected String dataElement;

    @JsonProperty("dataElement")
    public void setDataElement(Map<String, Object> dataElement) {
        this.dataElement = (String) dataElement.get("id");
    }

    public String getDataElement() {
        return dataElement;
    }

    public String getProgram() {
        return program;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean getExternalAccess() {
        return externalAccess;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public void setExternalAccess(boolean externalAccess) {
        this.externalAccess = externalAccess;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public void setDataElement(String dataElement) {
        this.dataElement = dataElement;
    }
}
