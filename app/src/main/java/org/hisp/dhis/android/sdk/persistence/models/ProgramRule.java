/*
 *  Copyright (c) 2016, University of Oslo
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
public class ProgramRule extends BaseMetaDataObject {

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

    @Column(name = "programStage")
    String programStage;

    @Column(name = "program")
    String program;

    @Column(name = "condition")
    String condition;

    @Column(name = "externalAction")
    boolean externalAction;

    @Column(name = "priority")
    Integer priority;

    @JsonIgnore
    List<ProgramRuleAction> programRuleActions;

    @JsonProperty("programStage")
    public void setProgramStage(Map<String, Object> programStage) {
        this.programStage = (String) programStage.get("id");
    }

    @JsonProperty("program")
    public void setProgram(Map<String, Object> program) {
        this.program = (String) program.get("id");
    }

    public List<ProgramRuleAction> getProgramRuleActions() {
        return new Select()
                .from(ProgramRuleAction.class)
                .where(Condition.column(ProgramRuleAction$Table
                        .PROGRAMRULE).is(id))
                .queryList();
    }

    public void setProgramRuleActions(List<ProgramRuleAction> programRuleActions) {
        this.programRuleActions = programRuleActions;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getProgramStage() {
        return programStage;
    }

    public void setProgramStage(String programStage) {
        this.programStage = programStage;
    }

    public boolean getExternalAction() {
        return externalAction;
    }

    public void setExternalAction(boolean externalAction) {
        this.externalAction = externalAction;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
