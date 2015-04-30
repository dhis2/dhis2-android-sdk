package org.hisp.dhis2.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.Map;

/**
 * @author Simen Skogly Russnes on 29.04.15.
 */
@Table
public class ProgramRuleAction extends BaseIdentifiableObject {

    public static final String TYPE_HIDEFIELD = "HIDEFIELD";

    @Column
    public String programRuleActionType;

    @Column
    public boolean externalAccess;

    @Column
    public String displayName;

    @Column
    public String programRule;

    @JsonProperty("programRule")
    public void setProgramRule(Map<String, Object> programRule) {
        this.programRule = (String) programRule.get("id");
    }

    @Column
    public String dataElement;

    @JsonProperty("dataElement")
    public void setDataElement(Map<String, Object> dataElement) {
        this.dataElement = (String) dataElement.get("id");
    }
}
