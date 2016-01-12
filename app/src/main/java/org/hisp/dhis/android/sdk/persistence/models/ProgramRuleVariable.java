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

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.utils.api.ProgramRuleVariableSourceType;
import org.hisp.dhis.android.sdk.utils.api.ValueType;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;

/**
 * @author Simen Skogly Russnes on 29.04.15.
 */
@Table(databaseName = Dhis2Database.NAME)
public class ProgramRuleVariable extends BaseMetaDataObject {

    @Column(name = "trackedEntityAttribute")
    String trackedEntityAttribute;

    @Column(name = "dataElement")
    String dataElement;

    @JsonProperty("programRuleVariableSourceType")
    @Column(name = "sourceType")
    ProgramRuleVariableSourceType sourceType;

    @Column(name = "externalAccess")
    boolean externalAccess;

    @Column(name = "program")
    String program;

    @Column(name = "programStage")
    String programStage;

    @JsonIgnore
    String variableValue;

    @JsonIgnore
    ValueType variableType;

    @JsonIgnore
    boolean hasValue;

    @JsonIgnore
    String variableEventDate;

    @JsonIgnore
    List<String> allValues;

    @JsonProperty("programStage")
    public void setProgramStage(Map<String, Object> programStage) {
        this.programStage = (String) programStage.get("id");
    }

    @JsonProperty("program")
    public void setProgram(Map<String, Object> program) {
        this.program = (String) program.get("id");
    }

    @JsonProperty("dataElement")
    public void setDataElement(Map<String, Object> dataElement) {
        this.dataElement = (String) dataElement.get("id");
    }

    @JsonProperty("trackedEntityAttribute")
    public void setTrackedEntityAttribute(Map<String, Object> trackedEntityAttribute) {
        this.trackedEntityAttribute = (String) trackedEntityAttribute.get("id");
    }

    public String getProgramStage() {
        return programStage;
    }

    public void setProgramStage(String programStage) {
        this.programStage = programStage;
    }

    public String getTrackedEntityAttribute() {
        return trackedEntityAttribute;
    }

    public void setTrackedEntityAttribute(String trackedEntityAttribute) {
        this.trackedEntityAttribute = trackedEntityAttribute;
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

    public ProgramRuleVariableSourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(ProgramRuleVariableSourceType sourceType) {
        this.sourceType = sourceType;
    }

    public String getVariableValue() {
        return variableValue;
    }

    public void setVariableValue(String variableValue) {
        this.variableValue = variableValue;
    }

    public ValueType getVariableType() {
        return variableType;
    }

    public void setVariableType(ValueType variableType) {
        this.variableType = variableType;
    }

    public boolean isHasValue() {
        return hasValue;
    }

    public void setHasValue(boolean hasValue) {
        this.hasValue = hasValue;
    }

    public String getVariableEventDate() {
        return variableEventDate;
    }

    public void setVariableEventDate(String variableEventDate) {
        this.variableEventDate = variableEventDate;
    }

    public List<String> getAllValues() {
        return allValues;
    }

    public void setAllValues(List<String> allValues) {
        this.allValues = allValues;
    }
}
