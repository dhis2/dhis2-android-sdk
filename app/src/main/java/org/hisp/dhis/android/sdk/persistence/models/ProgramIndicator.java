/*
 *  Copyright (c) 2015, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Simen Skogly Russnes on 27.03.15.
 */
@Table(databaseName = Dhis2Database.NAME)
public class ProgramIndicator extends BaseNameableObject {

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

    public static String SEP_OBJECT = ":";

    public static final String EXPRESSION_REGEXP = "(" + KEY_DATAELEMENT + "|" + KEY_ATTRIBUTE + "|" + KEY_PROGRAM_VARIABLE + "|" + KEY_CONSTANT + ")\\{(\\w+|" +
            INCIDENT_DATE + "|" + ENROLLMENT_DATE + "|" + CURRENT_DATE + ")" + SEPARATOR_ID + "?(\\w*)\\}";

    public static final Pattern EXPRESSION_PATTERN = Pattern.compile( EXPRESSION_REGEXP );
    public static final Pattern DATAELEMENT_PATTERN = Pattern.compile( KEY_DATAELEMENT + "\\{(\\w{11})" + SEPARATOR_ID + "(\\w{11})\\}" );
    public static final Pattern ATTRIBUTE_PATTERN = Pattern.compile( KEY_ATTRIBUTE + "\\{(\\w{11})\\}" );
    public static final Pattern VALUECOUNT_PATTERN = Pattern.compile( "V\\{(" + VAR_VALUE_COUNT + "|" + VAR_ZERO_POS_VALUE_COUNT + ")\\}" );

    public static final String VALID = "valid";

    public static final String EXPRESSION_NOT_WELL_FORMED = "expression_not_well_formed";

    @JsonProperty("code")
    @Column
    private String code;

    @JsonProperty("expression")
    @Column
    private String expression;

    @JsonProperty("displayDescription")
    @Column
    private String displayDescription;

    @JsonProperty("rootDate")
    @Column
    private String rootDate;

    @JsonProperty("externalAccess")
    @Column
    private boolean externalAccess;

    @JsonProperty("valueType")
    @Column
    private String valueType;

    @JsonProperty("displayName")
    @Column
    private String displayName;

    @JsonProperty("displayShortName")
    @Column
    private String displayShortName;

    @Column
    protected String program;

    @JsonProperty("program")
    public void setProgram(Map<String, Object> program) {
        this.program = (String) program.get("id");
    }

    /**
     * Can be null if not assigned to a programStage
     */
    @Column
    private String programStage;

    /**
     * Can be null if not assigned to a specific section
     */
    @Column
    private String section;


    public String getCode() {
        return code;
    }

    public String getExpression() {
        return expression;
    }

    public String getDisplayDescription() {
        return displayDescription;
    }

    public String getRootDate() {
        return rootDate;
    }

    public boolean getExternalAccess() {
        return externalAccess;
    }

    public String getValueType() {
        return valueType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDisplayShortName() {
        return displayShortName;
    }

    public String getProgram() {
        return program;
    }

    public String getSection() {
        return section;
    }

    public String getProgramStage() {
        return programStage;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public void setProgramStage(String programStage) {
        this.programStage = programStage;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public void setDisplayShortName(String displayShortName) {
        this.displayShortName = displayShortName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public boolean isExternalAccess() {
        return externalAccess;
    }

    public void setExternalAccess(boolean externalAccess) {
        this.externalAccess = externalAccess;
    }

    public void setRootDate(String rootDate) {
        this.rootDate = rootDate;
    }

    public void setDisplayDescription(String displayDescription) {
        this.displayDescription = displayDescription;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
