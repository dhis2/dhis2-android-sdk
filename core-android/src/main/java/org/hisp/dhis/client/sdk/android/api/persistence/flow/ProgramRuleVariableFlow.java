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
import org.hisp.dhis.client.sdk.android.common.Mapper;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleVariable;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleVariableSourceType;

@Table(database = DbDhis.class)
public final class ProgramRuleVariableFlow extends BaseIdentifiableObjectFlow {
    public static final Mapper<ProgramRuleVariable, ProgramRuleVariableFlow>
            MAPPER = new VariableMapper();

    private static final String DATA_ELEMENT_KEY = "dataElement";
    private static final String TRACKED_ENTITY_ATTRIBUTE_KEY = "trackedEntityAttribute";
    private static final String PROGRAM_KEY = "program";
    private static final String PROGRAM_STAGE_KEY = "programStage";

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(
                            columnName = PROGRAM_KEY, columnType = String.class,
                            foreignKeyColumnName = BaseIdentifiableObjectFlow.COLUMN_UID),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.NO_ACTION
    )
    ProgramFlow program;

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(
                            columnName = PROGRAM_STAGE_KEY, columnType = String.class,
                            foreignKeyColumnName = BaseIdentifiableObjectFlow.COLUMN_UID),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.NO_ACTION
    )
    ProgramStageFlow programStage;

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(
                            columnName = DATA_ELEMENT_KEY, columnType = String.class,
                            foreignKeyColumnName = BaseIdentifiableObjectFlow.COLUMN_UID),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.NO_ACTION
    )
    DataElementFlow dataElement;

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(
                            columnName = TRACKED_ENTITY_ATTRIBUTE_KEY, columnType = String.class,
                            foreignKeyColumnName = BaseIdentifiableObjectFlow.COLUMN_UID),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.NO_ACTION
    )
    TrackedEntityAttributeFlow trackedEntityAttribute;

    @Column
    ProgramRuleVariableSourceType sourceType;

    public ProgramRuleVariableFlow() {
        // empty constructor
    }

    public DataElementFlow getDataElement() {
        return dataElement;
    }

    public void setDataElement(DataElementFlow dataElement) {
        this.dataElement = dataElement;
    }

    public TrackedEntityAttributeFlow getTrackedEntityAttribute() {
        return trackedEntityAttribute;
    }

    public void setTrackedEntityAttribute(TrackedEntityAttributeFlow trackedEntityAttribute) {
        this.trackedEntityAttribute = trackedEntityAttribute;
    }

    public ProgramRuleVariableSourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(ProgramRuleVariableSourceType sourceType) {
        this.sourceType = sourceType;
    }

    public ProgramFlow getProgram() {
        return program;
    }

    public void setProgram(ProgramFlow program) {
        this.program = program;
    }

    public ProgramStageFlow getProgramStage() {
        return programStage;
    }

    public void setProgramStage(ProgramStageFlow programStage) {
        this.programStage = programStage;
    }

    private static class VariableMapper extends AbsMapper<ProgramRuleVariable, ProgramRuleVariableFlow> {

        @Override
        public ProgramRuleVariableFlow mapToDatabaseEntity(ProgramRuleVariable variable) {
            if (variable == null) {
                return null;
            }

            ProgramRuleVariableFlow programRuleVariableFlow = new ProgramRuleVariableFlow();
            programRuleVariableFlow.setId(variable.getId());
            programRuleVariableFlow.setUId(variable.getUId());
            programRuleVariableFlow.setCreated(variable.getCreated());
            programRuleVariableFlow.setLastUpdated(variable.getLastUpdated());
            programRuleVariableFlow.setName(variable.getName());
            programRuleVariableFlow.setDisplayName(variable.getDisplayName());
            programRuleVariableFlow.setAccess(variable.getAccess());
            programRuleVariableFlow.setSourceType(variable.getSourceType());
            programRuleVariableFlow.setProgram(ProgramFlow.MAPPER
                    .mapToDatabaseEntity(variable.getProgram()));
            programRuleVariableFlow.setProgramStage(ProgramStageFlow.MAPPER
                    .mapToDatabaseEntity(variable.getProgramStage()));
            programRuleVariableFlow.setDataElement(DataElementFlow.MAPPER
                    .mapToDatabaseEntity(variable.getDataElement()));
            programRuleVariableFlow.setTrackedEntityAttribute(TrackedEntityAttributeFlow.MAPPER
                    .mapToDatabaseEntity(variable.getTrackedEntityAttribute()));
            return programRuleVariableFlow;
        }

        @Override
        public ProgramRuleVariable mapToModel(ProgramRuleVariableFlow variableFlow) {
            if (variableFlow == null) {
                return null;
            }

            ProgramRuleVariable programRuleVariable = new ProgramRuleVariable();
            programRuleVariable.setId(variableFlow.getId());
            programRuleVariable.setUId(variableFlow.getUId());
            programRuleVariable.setCreated(variableFlow.getCreated());
            programRuleVariable.setLastUpdated(variableFlow.getLastUpdated());
            programRuleVariable.setName(variableFlow.getName());
            programRuleVariable.setDisplayName(variableFlow.getDisplayName());
            programRuleVariable.setAccess(variableFlow.getAccess());
            programRuleVariable.setSourceType(variableFlow.getSourceType());
            programRuleVariable.setProgramStage(ProgramStageFlow.MAPPER
                    .mapToModel(variableFlow.getProgramStage()));
            programRuleVariable.setProgramStage(ProgramStageFlow.MAPPER
                    .mapToModel(variableFlow.getProgramStage()));
            programRuleVariable.setDataElement(DataElementFlow.MAPPER
                    .mapToModel(variableFlow.getDataElement()));
            programRuleVariable.setTrackedEntityAttribute(TrackedEntityAttributeFlow.MAPPER
                    .mapToModel(variableFlow.getTrackedEntityAttribute()));
            return programRuleVariable;
        }

        @Override
        public Class<ProgramRuleVariable> getModelTypeClass() {
            return ProgramRuleVariable.class;
        }

        @Override
        public Class<ProgramRuleVariableFlow> getDatabaseEntityTypeClass() {
            return ProgramRuleVariableFlow.class;
        }
    }
}
