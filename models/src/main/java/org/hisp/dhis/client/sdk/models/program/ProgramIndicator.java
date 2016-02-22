/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.client.sdk.models.program;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.hisp.dhis.client.sdk.models.common.ValueType;
import org.hisp.dhis.client.sdk.models.common.base.BaseNameableObject;

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
    private ValueType valueType;

    @JsonProperty("displayShortName")
    private String displayShortName;

    @JsonProperty("program")
    private Program program;

    private ProgramStage programStage;

    private ProgramStageSection programStageSection;

    public ProgramStage getProgramStage() {
        return programStage;
    }

    public void setProgramStage(ProgramStage programStage) {
        this.programStage = programStage;
    }

    public ProgramStageSection getProgramStageSection() {
        return programStageSection;
    }

    public void setProgramStageSection(ProgramStageSection programStageSection) {
        this.programStageSection = programStageSection;
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

    public ValueType getValueType() {
        return valueType;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    public String getDisplayShortName() {
        return displayShortName;
    }

    public void setDisplayShortName(String displayShortName) {
        this.displayShortName = displayShortName;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }
}
