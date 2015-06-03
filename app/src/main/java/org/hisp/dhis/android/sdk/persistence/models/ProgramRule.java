package org.hisp.dhis.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Simen Skogly Russnes on 29.04.15.
 */
@Table(databaseName = Dhis2Database.NAME)
public class ProgramRule extends BaseIdentifiableObject {

    public static final String SEPARATOR_ID = "\\.";
    public static final String KEY_DATAELEMENT = "#";
    public static final String KEY_ATTRIBUTE = "A";
    public static final String KEY_PROGRAM_VARIABLE = "V";
    public static final String KEY_CONSTANT = "C";
    public static final String INCIDENT_DATE = "incident_date";
    public static final String ENROLLMENT_DATE = "enrollment_date";
    public static final String CURRENT_DATE = "current_date";
    public static final String VALUE_COUNT = "value_count";
    public static final String VAR_VALUE_COUNT = "value_count";
    public static final String VAR_ZERO_POS_VALUE_COUNT = "zero_pos_value_count";
    public static final String VALUE_TYPE_DATE = "date";
    public static final String VALUE_TYPE_INT = "int";

    public static final String EXPRESSION_REGEXP = "(" + KEY_DATAELEMENT + "|" + KEY_ATTRIBUTE + "|" + KEY_PROGRAM_VARIABLE + "|" + KEY_CONSTANT + ")\\{(\\w+|" +
            INCIDENT_DATE + "|" + ENROLLMENT_DATE + "|" + CURRENT_DATE + ")" + SEPARATOR_ID + "?(\\w*)\\}";

    public static final Pattern EXPRESSION_PATTERN = Pattern.compile( EXPRESSION_REGEXP );
    public static final Pattern DATAELEMENT_PATTERN = Pattern.compile( KEY_DATAELEMENT + "\\{(\\w{11})" + SEPARATOR_ID + "(\\w{11})\\}" );

    @Column
    private String condition;

    @Column
    private boolean externalAction;

    @Column
    private String displayName;

    @Column
    protected String programStage;

    @JsonProperty("programStage")
    public void setProgramStage(Map<String, Object> programStage) {
        this.programStage = (String) programStage.get("id");
    }

    @Column
    protected String program;

    @JsonProperty("program")
    public void setProgram(Map<String, Object> program) {
        this.program = (String) program.get("id");
    }

    @JsonIgnore
    private List<ProgramRuleAction> programRuleActions;

    public List<ProgramRuleAction> getProgramRuleActions() {
        return new Select().from(ProgramRuleAction.class).where
                (Condition.column(ProgramRuleAction$Table.PROGRAMRULE).is(id)).queryList();
    }

    public String getProgram() {
        return program;
    }

    public String getProgramStage() {
        return programStage;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean getExternalAction() {
        return externalAction;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setExternalAction(boolean externalAction) {
        this.externalAction = externalAction;
    }

    public void setProgramStage(String programStage) {
        this.programStage = programStage;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public void setProgramRuleActions(List<ProgramRuleAction> programRuleActions) {
        this.programRuleActions = programRuleActions;
    }
}
