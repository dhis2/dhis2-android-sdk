package org.hisp.dhis.android.sdk.models.programindicator;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.hisp.dhis.android.sdk.models.common.meta.BaseNameableObject;

import java.util.Map;
import java.util.regex.Pattern;

public final class ProgramIndicator extends BaseNameableObject {

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
    public static final Pattern EXPRESSION_PATTERN = Pattern.compile(EXPRESSION_REGEXP);
    public static final Pattern DATAELEMENT_PATTERN = Pattern.compile(KEY_DATAELEMENT + "\\{(\\w{11})" + SEPARATOR_ID + "(\\w{11})\\}");
    public static final Pattern ATTRIBUTE_PATTERN = Pattern.compile(KEY_ATTRIBUTE + "\\{(\\w{11})\\}");
    public static final Pattern VALUECOUNT_PATTERN = Pattern.compile("V\\{(" + VAR_VALUE_COUNT + "|" + VAR_ZERO_POS_VALUE_COUNT + ")\\}");
    public static final String VALID = "valid";
    public static final String EXPRESSION_NOT_WELL_FORMED = "expression_not_well_formed";
    public static String SEP_OBJECT = ":";

    @JsonProperty("code")
    private String code;

    @JsonProperty("expression")
    private String expression;

    @JsonProperty("displayDescription")
    private String displayDescription;

    @JsonProperty("rootDate")
    private String rootDate;

    @JsonProperty("externalAccess")
    private boolean externalAccess;

    @JsonProperty("valueType")
    private String valueType;

    @JsonProperty("displayShortName")
    private String displayShortName;

    private String program;

    @JsonProperty("program")
    public void setProgramFromJSON(Map<String, Object> program) {
        this.program = (String) program.get("id");
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getDisplayDescription() {
        return displayDescription;
    }

    public void setDisplayDescription(String displayDescription) {
        this.displayDescription = displayDescription;
    }

    public String getRootDate() {
        return rootDate;
    }

    public void setRootDate(String rootDate) {
        this.rootDate = rootDate;
    }

    public boolean isExternalAccess() {
        return externalAccess;
    }

    public void setExternalAccess(boolean externalAccess) {
        this.externalAccess = externalAccess;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getDisplayShortName() {
        return displayShortName;
    }

    public void setDisplayShortName(String displayShortName) {
        this.displayShortName = displayShortName;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }
}
