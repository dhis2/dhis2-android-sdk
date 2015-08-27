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
public class ProgramRuleVariable extends BaseMetaDataObject {

    @Column(name = "dataElement")
    String dataElement;

    @JsonProperty("programRuleVariableSourceType")
    @Column(name = "sourceType")
    String sourceType;

    @Column(name = "externalAccess")
    boolean externalAccess;

    @Column(name = "program")
    String program;

    @JsonProperty("program")
    public void setProgram(Map<String, Object> program) {
        this.program = (String) program.get("id");
    }

    @JsonProperty("dataElement")
    public void setDataElement(Map<String, Object> dataElement) {
        this.dataElement = (String) dataElement.get("id");
    }

    public String getDataElement() {
        return dataElement;
    }

    public void setDataElement(String dataElement) {
        this.dataElement = dataElement;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public boolean getExternalAccess() {
        return externalAccess;
    }

    public void setExternalAccess(boolean externalAccess) {
        this.externalAccess = externalAccess;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }
}
