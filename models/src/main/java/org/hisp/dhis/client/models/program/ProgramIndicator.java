package org.hisp.dhis.client.models.program;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.client.models.common.BaseNameableObject;
import org.hisp.dhis.client.models.common.ValueType;

import java.util.regex.Pattern;

import javax.annotation.Nullable;

@AutoValue
@JsonDeserialize(builder = AutoValue_ProgramIndicator.Builder.class)
public abstract class ProgramIndicator extends BaseNameableObject {

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
    public static final String EXPRESSION_REGEXP = "(" + KEY_DATAELEMENT + "|" + KEY_ATTRIBUTE +
            "|" + KEY_PROGRAM_VARIABLE + "|" + KEY_CONSTANT + ")\\{(\\w+|" +
            INCIDENT_DATE + "|" + ENROLLMENT_DATE + "|" + CURRENT_DATE + ")" + SEPARATOR_ID + "?" +
            "(\\w*)\\}";
    public static final Pattern EXPRESSION_PATTERN = Pattern.compile(EXPRESSION_REGEXP);
    public static final Pattern DATAELEMENT_PATTERN = Pattern.compile(KEY_DATAELEMENT + "\\{" +
            "(\\w{11})" + SEPARATOR_ID + "(\\w{11})\\}");
    public static final Pattern ATTRIBUTE_PATTERN = Pattern.compile(KEY_ATTRIBUTE + "\\{(\\w{11})" +
            "\\}");
    public static final Pattern VALUECOUNT_PATTERN = Pattern.compile("V\\{(" + VAR_VALUE_COUNT +
            "|" + VAR_ZERO_POS_VALUE_COUNT + ")\\}");
    public static final String VALID = "valid";
    public static final String EXPRESSION_NOT_WELL_FORMED = "expression_not_well_formed";
    public static String SEP_OBJECT = ":";

    // json mapping Property strings:
    private static final String CODE = "code";
    private static final String EXPRESSION = "expression";
    private static final String DISPLAY_DESCRIPTION = "displayDescription";
    private static final String ROOT_DATE = "rootDate";
    private static final String EXTERNAL_ACCESS = "externalAccess";
    private static final String VALUE_TYPE = "valueType";
    private static final String DISPLAY_SHORT_NAME = "displayShortName";
    private static final String PROGRAM = "program";

    @Nullable
    @JsonProperty(CODE)
    public abstract String code();

    @Nullable
    @JsonProperty(EXPRESSION)
    public abstract String expression();

    @Nullable
    @JsonProperty(DISPLAY_DESCRIPTION)
    public abstract String displayDescription();

    @Nullable
    @JsonProperty(ROOT_DATE)
    public abstract String rootDate();

    @Nullable
    @JsonProperty(EXTERNAL_ACCESS)
    public abstract Boolean externalAccess();

    @Nullable
    @JsonProperty(VALUE_TYPE)
    public abstract ValueType valueType();

    @Nullable
    @JsonProperty(DISPLAY_SHORT_NAME)
    public abstract String displayShortName();

    @Nullable
    @JsonProperty(PROGRAM)
    public abstract Program program();

    @AutoValue.Builder
    public static abstract class Builder extends BaseNameableObject.Builder<ProgramIndicator.Builder> {
        @JsonProperty(CODE)
        public abstract ProgramIndicator.Builder code(@Nullable String code);

        @JsonProperty(EXPRESSION)
        public abstract ProgramIndicator.Builder expression(@Nullable String expression);

        @JsonProperty(DISPLAY_DESCRIPTION)
        public abstract ProgramIndicator.Builder displayDescription(@Nullable String displayDescription);

        @JsonProperty(ROOT_DATE)
        public abstract ProgramIndicator.Builder rootDate(@Nullable String rootDate);

        @JsonProperty(EXTERNAL_ACCESS)
        public abstract ProgramIndicator.Builder externalAccess(@Nullable Boolean externalAccess);

        @JsonProperty(VALUE_TYPE)
        public abstract ProgramIndicator.Builder valueType(@Nullable ValueType valueType);

        @JsonProperty(DISPLAY_SHORT_NAME)
        public abstract ProgramIndicator.Builder displayShortName(@Nullable String displayShortName);

        @JsonProperty(PROGRAM)
        public abstract ProgramIndicator.Builder program(@Nullable Program program);

        abstract ProgramIndicator build();
    }
}