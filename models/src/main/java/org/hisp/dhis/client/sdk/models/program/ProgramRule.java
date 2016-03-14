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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.hisp.dhis.client.sdk.models.common.base.BaseIdentifiableObject;

import java.util.Comparator;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class ProgramRule extends BaseIdentifiableObject {

    @JsonProperty("programStage")
    private ProgramStage programStage;

    @JsonProperty("program")
    private Program program;

    @JsonProperty("description")
    private String description;

    @JsonProperty("condition")
    private String condition;

    @JsonProperty("priority")
    private Integer priority;

    @JsonProperty("externalAction")
    private boolean externalAction;

    @JsonProperty("programRuleActions")
    private List<ProgramRuleAction> programRuleActions;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public ProgramStage getProgramStage() {
        return programStage;
    }

    public void setProgramStage(ProgramStage programStage) {
        this.programStage = programStage;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public boolean isExternalAction() {
        return externalAction;
    }

    public void setExternalAction(boolean externalAction) {
        this.externalAction = externalAction;
    }

    public List<ProgramRuleAction> getProgramRuleActions() {
        return programRuleActions;
    }

    public void setProgramRuleActions(List<ProgramRuleAction> programRuleActions) {
        this.programRuleActions = programRuleActions;
    }

    public static class PriorityComparator implements Comparator<ProgramRule> {

        @Override
        public int compare(ProgramRule first, ProgramRule second) {
            if (first == null && second == null) {
                return 0;
            } else if (first == null) {
                return 1;
            } else if (second == null) {
                return -1;
            }

            if (first.getPriority() == null && second.getPriority() == null) {
                return 0;
            } else if (first.getPriority() == null) {
                return 1;
            } else if (second.getPriority() == null) {
                return -1;
            }

            if (first.getPriority() < second.getPriority()) {
                return -1;
            } else if (first.getPriority().equals(second.getPriority())) {
                return 0;
            } else {
                return 1;
            }
        }
    }
}
