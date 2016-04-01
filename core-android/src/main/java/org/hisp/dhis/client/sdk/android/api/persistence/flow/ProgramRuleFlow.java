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

package org.hisp.dhis.client.sdk.android.api.persistence.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.client.sdk.android.api.persistence.DbDhis;
import org.hisp.dhis.client.sdk.android.common.AbsMapper;
import org.hisp.dhis.client.sdk.android.common.IMapper;
import org.hisp.dhis.client.sdk.models.program.ProgramRule;

import java.util.List;

@Table(database = DbDhis.class)
public final class ProgramRuleFlow extends BaseIdentifiableObjectFlow {
    public static final IMapper<ProgramRule, ProgramRuleFlow> MAPPER = new Mapper();


    private static final String PROGRAM_STAGE_KEY = "programstage";
    private static final String PROGRAM_KEY = "program";

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = PROGRAM_STAGE_KEY,
                            columnType = String.class, foreignKeyColumnName = "uId"),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.NO_ACTION
    )
    ProgramStageFlow programStage;

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = PROGRAM_KEY,
                            columnType = String.class, foreignKeyColumnName = "uId"),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.NO_ACTION
    )
    ProgramFlow program;

    @Column
    String condition;

    @Column
    String description;

    @Column
    Integer priority;

    @Column
    boolean externalAction;

    List<ProgramRuleActionFlow> programRuleActions;

    public ProgramRuleFlow() {
        // empty constructor
    }

    public ProgramStageFlow getProgramStage() {
        return programStage;
    }

    public void setProgramStage(ProgramStageFlow programStage) {
        this.programStage = programStage;
    }

    public ProgramFlow getProgram() {
        return program;
    }

    public void setProgram(ProgramFlow program) {
        this.program = program;
    }

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

    public List<ProgramRuleActionFlow> getProgramRuleActions() {
        return programRuleActions;
    }

    public void setProgramRuleActions(List<ProgramRuleActionFlow> programRuleActions) {
        this.programRuleActions = programRuleActions;
    }

    private static class Mapper extends AbsMapper<ProgramRule, ProgramRuleFlow> {

        @Override
        public ProgramRuleFlow mapToDatabaseEntity(ProgramRule programRule) {
            if (programRule == null) {
                return null;
            }

            ProgramRuleFlow programRuleFlow = new ProgramRuleFlow();
            programRuleFlow.setId(programRule.getId());
            programRuleFlow.setUId(programRule.getUId());
            programRuleFlow.setCreated(programRule.getCreated());
            programRuleFlow.setLastUpdated(programRule.getLastUpdated());
            programRuleFlow.setName(programRule.getName());
            programRuleFlow.setDisplayName(programRule.getDisplayName());
            programRuleFlow.setAccess(programRule.getAccess());
            programRuleFlow.setCondition(programRule.getCondition());
            programRuleFlow.setDescription(programRule.getDescription());
            programRuleFlow.setExternalAction(programRule.isExternalAction());
            programRuleFlow.setPriority(programRule.getPriority());
            programRuleFlow.setProgramStage(ProgramStageFlow.MAPPER
                    .mapToDatabaseEntity(programRule.getProgramStage()));
            programRuleFlow.setProgram(ProgramFlow.MAPPER
                    .mapToDatabaseEntity(programRule.getProgram()));
            return programRuleFlow;
        }

        @Override
        public ProgramRule mapToModel(ProgramRuleFlow programRuleFlow) {
            if (programRuleFlow == null) {
                return null;
            }

            ProgramRule programRule = new ProgramRule();
            programRule.setId(programRuleFlow.getId());
            programRule.setUId(programRuleFlow.getUId());
            programRule.setCreated(programRuleFlow.getCreated());
            programRule.setLastUpdated(programRuleFlow.getLastUpdated());
            programRule.setName(programRuleFlow.getName());
            programRule.setDisplayName(programRuleFlow.getDisplayName());
            programRule.setAccess(programRuleFlow.getAccess());
            programRule.setCondition(programRuleFlow.getCondition());
            programRule.setDescription(programRuleFlow.getDescription());
            programRule.setExternalAction(programRuleFlow.isExternalAction());
            programRule.setPriority(programRuleFlow.getPriority());
            programRule.setProgramStage(ProgramStageFlow.MAPPER
                    .mapToModel(programRuleFlow.getProgramStage()));
            programRule.setProgram(ProgramFlow.MAPPER
                    .mapToModel(programRuleFlow.getProgram()));
            return programRule;
        }

        @Override
        public Class<ProgramRule> getModelTypeClass() {
            return ProgramRule.class;
        }

        @Override
        public Class<ProgramRuleFlow> getDatabaseEntityTypeClass() {
            return ProgramRuleFlow.class;
        }
    }
}
