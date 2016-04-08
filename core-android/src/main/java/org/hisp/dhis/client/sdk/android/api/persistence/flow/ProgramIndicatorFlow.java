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
import org.hisp.dhis.client.sdk.models.common.ValueType;
import org.hisp.dhis.client.sdk.models.program.ProgramIndicator;

@Table(database = DbDhis.class)
public final class ProgramIndicatorFlow extends BaseIdentifiableObjectFlow {
    public static final org.hisp.dhis.client.sdk.android.common.Mapper<ProgramIndicator, ProgramIndicatorFlow> MAPPER = new Mapper();

    static final String PROGRAM_KEY = "program";
    static final String PROGRAM_STAGE_KEY = "programstage";
    static final String PROGRAM_STAGE_SECTION_KEY = "programstagesection";

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = PROGRAM_KEY, columnType = String.class,
                            foreignKeyColumnName = "uId"),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
    ProgramFlow program;

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = PROGRAM_STAGE_KEY, columnType = String.class,
                            foreignKeyColumnName = "uId"),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.SET_NULL
    )
    ProgramStageFlow programStage;

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = PROGRAM_STAGE_SECTION_KEY,
                            columnType = String.class, foreignKeyColumnName = "uId"),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.SET_NULL
    )
    ProgramStageSectionFlow programStageSection;

    @Column
    String code;

    @Column
    String expression;

    @Column
    String displayDescription;

    @Column
    String rootDate;

    @Column
    boolean externalAccess;

    @Column
    ValueType valueType;

    @Column
    String displayShortName;

    public ProgramIndicatorFlow() {
        // empty constructor
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

    public ProgramFlow getProgram() {
        return program;
    }

    public void setProgram(ProgramFlow program) {
        this.program = program;
    }

    public ProgramStageSectionFlow getProgramStageSection() {
        return programStageSection;
    }

    public void setProgramStageSection(ProgramStageSectionFlow programStageSection) {
        this.programStageSection = programStageSection;
    }

    public ProgramStageFlow getProgramStage() {
        return programStage;
    }

    public void setProgramStage(ProgramStageFlow programStage) {
        this.programStage = programStage;
    }

    private static class Mapper extends AbsMapper<ProgramIndicator, ProgramIndicatorFlow> {

        @Override
        public ProgramIndicatorFlow mapToDatabaseEntity(ProgramIndicator programIndicator) {
            if (programIndicator == null) {
                return null;
            }

            ProgramIndicatorFlow programIndicatorFlow = new ProgramIndicatorFlow();
            programIndicatorFlow.setId(programIndicator.getId());
            programIndicatorFlow.setUId(programIndicator.getUId());
            programIndicatorFlow.setCreated(programIndicator.getCreated());
            programIndicatorFlow.setLastUpdated(programIndicator.getLastUpdated());
            programIndicatorFlow.setName(programIndicator.getName());
            programIndicatorFlow.setDisplayName(programIndicator.getDisplayName());
            programIndicatorFlow.setAccess(programIndicator.getAccess());
            programIndicatorFlow.setExternalAccess(programIndicator.isExternalAccess());
            programIndicatorFlow.setCode(programIndicator.getCode());
            programIndicatorFlow.setDisplayDescription(programIndicator.getDisplayDescription());
            programIndicatorFlow.setDisplayShortName(programIndicator.getDisplayShortName());
            programIndicatorFlow.setExpression(programIndicator.getExpression());
            programIndicatorFlow.setRootDate(programIndicator.getRootDate());
            programIndicatorFlow.setValueType(programIndicator.getValueType());
            programIndicatorFlow.setProgram(ProgramFlow.MAPPER
                    .mapToDatabaseEntity(programIndicator.getProgram()));
            programIndicatorFlow.setProgramStage(ProgramStageFlow.MAPPER
                    .mapToDatabaseEntity(programIndicator.getProgramStage()));
            programIndicatorFlow.setProgramStageSection(ProgramStageSectionFlow.MAPPER
                    .mapToDatabaseEntity(programIndicator.getProgramStageSection()));
            return programIndicatorFlow;
        }

        @Override
        public ProgramIndicator mapToModel(ProgramIndicatorFlow programIndicatorFlow) {
            if (programIndicatorFlow == null) {
                return null;
            }

            ProgramIndicator programIndicator = new ProgramIndicator();
            programIndicator.setId(programIndicatorFlow.getId());
            programIndicator.setUId(programIndicatorFlow.getUId());
            programIndicator.setCreated(programIndicatorFlow.getCreated());
            programIndicator.setLastUpdated(programIndicatorFlow.getLastUpdated());
            programIndicator.setName(programIndicatorFlow.getName());
            programIndicator.setDisplayName(programIndicatorFlow.getDisplayName());
            programIndicator.setAccess(programIndicatorFlow.getAccess());
            programIndicator.setExternalAccess(programIndicatorFlow.isExternalAccess());
            programIndicator.setCode(programIndicatorFlow.getCode());
            programIndicator.setDisplayDescription(programIndicatorFlow.getDisplayDescription());
            programIndicator.setDisplayShortName(programIndicatorFlow.getDisplayShortName());
            programIndicator.setExpression(programIndicatorFlow.getExpression());
            programIndicator.setRootDate(programIndicatorFlow.getRootDate());
            programIndicator.setValueType(programIndicatorFlow.getValueType());
            programIndicator.setProgramStage(ProgramStageFlow.MAPPER
                    .mapToModel(programIndicatorFlow.getProgramStage()));
            programIndicator.setProgramStage(ProgramStageFlow.MAPPER
                    .mapToModel(programIndicatorFlow.getProgramStage()));
            programIndicator.setProgramStageSection(ProgramStageSectionFlow.MAPPER
                    .mapToModel(programIndicatorFlow.getProgramStageSection()));
            return programIndicator;
        }

        @Override
        public Class<ProgramIndicator> getModelTypeClass() {
            return ProgramIndicator.class;
        }

        @Override
        public Class<ProgramIndicatorFlow> getDatabaseEntityTypeClass() {
            return ProgramIndicatorFlow.class;
        }
    }
}
