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

package org.hisp.dhis.client.sdk.rules;

import org.hisp.dhis.client.sdk.models.dataelement.DataElement;
import org.hisp.dhis.client.sdk.models.program.ProgramIndicator;
import org.hisp.dhis.client.sdk.models.program.ProgramRule;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleActionType;
import org.hisp.dhis.client.sdk.models.program.ProgramStage;
import org.hisp.dhis.client.sdk.models.program.ProgramStageSection;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttribute;

public class RuleEffect {

    private ProgramRule programRule;

    private TrackedEntityAttribute trackedEntityAttribute;

    private DataElement dataElement;

    private ProgramIndicator programIndicator;

    private ProgramStage programStage;

    private ProgramStageSection programStageSection;

    private ProgramRuleActionType programRuleActionType;

    private String content;

    private String location;

    private String data;

    public ProgramRule getProgramRule() {
        return programRule;
    }

    public void setProgramRule(ProgramRule programRule) {
        this.programRule = programRule;
    }

    public TrackedEntityAttribute getTrackedEntityAttribute() {
        return trackedEntityAttribute;
    }

    public void setTrackedEntityAttribute(TrackedEntityAttribute trackedEntityAttribute) {
        this.trackedEntityAttribute = trackedEntityAttribute;
    }

    public DataElement getDataElement() {
        return dataElement;
    }

    public void setDataElement(DataElement dataElement) {
        this.dataElement = dataElement;
    }

    public ProgramStageSection getProgramStageSection() {
        return programStageSection;
    }

    public void setProgramStageSection(ProgramStageSection programStageSection) {
        this.programStageSection = programStageSection;
    }

    public ProgramRuleActionType getProgramRuleActionType() {
        return programRuleActionType;
    }

    public void setProgramRuleActionType(ProgramRuleActionType programRuleActionType) {
        this.programRuleActionType = programRuleActionType;
    }

    public ProgramIndicator getProgramIndicator() {
        return programIndicator;
    }

    public void setProgramIndicator(ProgramIndicator programIndicator) {
        this.programIndicator = programIndicator;
    }

    public ProgramStage getProgramStage() {
        return programStage;
    }

    public void setProgramStage(ProgramStage programStage) {
        this.programStage = programStage;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RuleEffect{" +
                "programRule=" + programRule +
                ", trackedEntityAttribute=" + trackedEntityAttribute +
                ", dataElement=" + dataElement +
                ", programIndicator=" + programIndicator +
                ", programStage=" + programStage +
                ", programStageSection=" + programStageSection +
                ", programRuleActionType=" + programRuleActionType +
                ", content='" + content + '\'' +
                ", location='" + location + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
