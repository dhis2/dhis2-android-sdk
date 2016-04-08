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
import org.hisp.dhis.client.sdk.models.program.ProgramRuleVariable;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleVariableSourceType;

@Table(database = DbDhis.class)
public final class ProgramRuleVariableFlow extends BaseIdentifiableObjectFlow {
    public static final org.hisp.dhis.client.sdk.android.common.Mapper<ProgramRuleVariable, ProgramRuleVariableFlow> MAPPER = new Mapper();

    private static final String DATA_ELEMENT_KEY = "dataelement";
    private static final String TRACKED_ENTITY_ATTRIBUTE_KEY = "trackedentityattribute";
    private static final String PROGRAM_KEY = "program";
    private static final String PROGRAM_STAGE_KEY = "programStage";

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = PROGRAM_KEY,
                            columnType = String.class, foreignKeyColumnName = "uId"),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.NO_ACTION
    )
    ProgramFlow program;

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
                    @ForeignKeyReference(columnName = DATA_ELEMENT_KEY,
                            columnType = String.class, foreignKeyColumnName = "uId"),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.NO_ACTION
    )
    DataElementFlow dataElement;

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = TRACKED_ENTITY_ATTRIBUTE_KEY,
                            columnType = String.class, foreignKeyColumnName = "uId"),
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

    private static class Mapper extends AbsMapper<ProgramRuleVariable, ProgramRuleVariableFlow> {

        @Override
        public ProgramRuleVariableFlow mapToDatabaseEntity(ProgramRuleVariable programRuleVariable) {
            if (programRuleVariable == null) {
                return null;
            }

            ProgramRuleVariableFlow programRuleVariableFlow = new ProgramRuleVariableFlow();
            programRuleVariableFlow.setId(programRuleVariable.getId());
            programRuleVariableFlow.setUId(programRuleVariable.getUId());
            programRuleVariableFlow.setCreated(programRuleVariable.getCreated());
            programRuleVariableFlow.setLastUpdated(programRuleVariable.getLastUpdated());
            programRuleVariableFlow.setName(programRuleVariable.getName());
            programRuleVariableFlow.setDisplayName(programRuleVariable.getDisplayName());
            programRuleVariableFlow.setAccess(programRuleVariable.getAccess());
            programRuleVariableFlow.setSourceType(programRuleVariable.getSourceType());
            programRuleVariableFlow.setProgram(ProgramFlow.MAPPER
                    .mapToDatabaseEntity(programRuleVariable.getProgram()));
            programRuleVariableFlow.setProgramStage(ProgramStageFlow.MAPPER
                    .mapToDatabaseEntity(programRuleVariable.getProgramStage()));
            programRuleVariableFlow.setDataElement(DataElementFlow.MAPPER
                    .mapToDatabaseEntity(programRuleVariable.getDataElement()));
            programRuleVariableFlow.setTrackedEntityAttribute(TrackedEntityAttributeFlow.MAPPER
                    .mapToDatabaseEntity(programRuleVariable.getTrackedEntityAttribute()));
            return programRuleVariableFlow;
        }

        @Override
        public ProgramRuleVariable mapToModel(ProgramRuleVariableFlow programIndicatorFlow) {
            if (programIndicatorFlow == null) {
                return null;
            }

            ProgramRuleVariable programRuleVariable = new ProgramRuleVariable();
            programRuleVariable.setId(programIndicatorFlow.getId());
            programRuleVariable.setUId(programIndicatorFlow.getUId());
            programRuleVariable.setCreated(programIndicatorFlow.getCreated());
            programRuleVariable.setLastUpdated(programIndicatorFlow.getLastUpdated());
            programRuleVariable.setName(programIndicatorFlow.getName());
            programRuleVariable.setDisplayName(programIndicatorFlow.getDisplayName());
            programRuleVariable.setAccess(programIndicatorFlow.getAccess());
            programRuleVariable.setSourceType(programIndicatorFlow.getSourceType());
            programRuleVariable.setProgramStage(ProgramStageFlow.MAPPER
                    .mapToModel(programIndicatorFlow.getProgramStage()));
            programRuleVariable.setProgramStage(ProgramStageFlow.MAPPER
                    .mapToModel(programIndicatorFlow.getProgramStage()));
            programRuleVariable.setDataElement(DataElementFlow.MAPPER
                    .mapToModel(programIndicatorFlow.getDataElement()));
            programRuleVariable.setTrackedEntityAttribute(TrackedEntityAttributeFlow.MAPPER
                    .mapToModel(programIndicatorFlow.getTrackedEntityAttribute()));
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
