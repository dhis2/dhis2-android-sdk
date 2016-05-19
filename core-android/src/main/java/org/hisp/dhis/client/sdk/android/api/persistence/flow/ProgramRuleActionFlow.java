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
import org.hisp.dhis.client.sdk.models.program.ProgramRuleAction;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleActionType;

@Table(database = DbDhis.class)
public final class ProgramRuleActionFlow extends BaseIdentifiableObjectFlow {
    public static final Mapper<ProgramRuleAction, ProgramRuleActionFlow> MAPPER = new ActionMapper();

    private static final String PROGRAM_RULE_KEY = "programRule";
    private static final String PROGRAM_STAGE_KEY = "programStage";
    private static final String PROGRAM_STAGE_SECTION_KEY = "programStageSection";
    private static final String PROGRAM_INDICATOR_KEY = "programIndicator";
    private static final String TRACKED_ENTITY_ATTRIBUTE_KEY = "trackedEntityAttribute";
    private static final String DATA_ELEMENT_KEY = "dataElement";

    @Column
    ProgramRuleActionType programRuleActionType;

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = PROGRAM_RULE_KEY, columnType = String.class,
                            foreignKeyColumnName = BaseIdentifiableObjectFlow.COLUMN_UID),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.NO_ACTION
    )
    ProgramRuleFlow programRule;

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
                            columnName = PROGRAM_STAGE_SECTION_KEY, columnType = String.class,
                            foreignKeyColumnName = BaseIdentifiableObjectFlow.COLUMN_UID),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.NO_ACTION
    )
    ProgramStageSectionFlow programStageSection;

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(
                            columnName = PROGRAM_INDICATOR_KEY, columnType = String.class,
                            foreignKeyColumnName = BaseIdentifiableObjectFlow.COLUMN_UID),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.NO_ACTION
    )
    ProgramIndicatorFlow programIndicator;

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
    @ForeignKey(
            references = {
                    @ForeignKeyReference(
                            columnName = DATA_ELEMENT_KEY, columnType = String.class,
                            foreignKeyColumnName = BaseIdentifiableObjectFlow.COLUMN_UID),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.NO_ACTION
    )
    DataElementFlow dataElement;

    @Column
    String content;

    @Column
    String location;

    @Column
    String data;

    public ProgramRuleActionFlow() {
        // empty constructor
    }

    public ProgramRuleFlow getProgramRule() {
        return programRule;
    }

    public void setProgramRule(ProgramRuleFlow programRule) {
        this.programRule = programRule;
    }

    public TrackedEntityAttributeFlow getTrackedEntityAttribute() {
        return trackedEntityAttribute;
    }

    public void setTrackedEntityAttribute(TrackedEntityAttributeFlow trackedEntityAttribute) {
        this.trackedEntityAttribute = trackedEntityAttribute;
    }

    public DataElementFlow getDataElement() {
        return dataElement;
    }

    public void setDataElement(DataElementFlow dataElement) {
        this.dataElement = dataElement;
    }

    public ProgramIndicatorFlow getProgramIndicator() {
        return programIndicator;
    }

    public void setProgramIndicator(ProgramIndicatorFlow programIndicator) {
        this.programIndicator = programIndicator;
    }

    public ProgramStageFlow getProgramStage() {
        return programStage;
    }

    public void setProgramStage(ProgramStageFlow programStage) {
        this.programStage = programStage;
    }

    public ProgramStageSectionFlow getProgramStageSection() {
        return programStageSection;
    }

    public void setProgramStageSection(ProgramStageSectionFlow programStageSection) {
        this.programStageSection = programStageSection;
    }

    public ProgramRuleActionType getProgramRuleActionType() {
        return programRuleActionType;
    }

    public void setProgramRuleActionType(ProgramRuleActionType programRuleActionType) {
        this.programRuleActionType = programRuleActionType;
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

    private static class ActionMapper extends AbsMapper<ProgramRuleAction, ProgramRuleActionFlow> {

        @Override
        public ProgramRuleActionFlow mapToDatabaseEntity(ProgramRuleAction programRuleAction) {
            if (programRuleAction == null) {
                return null;
            }

            ProgramRuleActionFlow programRuleActionFlow = new ProgramRuleActionFlow();
            programRuleActionFlow.setId(programRuleAction.getId());
            programRuleActionFlow.setUId(programRuleAction.getUId());
            programRuleActionFlow.setCreated(programRuleAction.getCreated());
            programRuleActionFlow.setLastUpdated(programRuleAction.getLastUpdated());
            programRuleActionFlow.setAccess(programRuleAction.getAccess());

            programRuleActionFlow.setProgramRule(ProgramRuleFlow.MAPPER
                    .mapToDatabaseEntity(programRuleAction.getProgramRule()));
            programRuleActionFlow.setTrackedEntityAttribute(TrackedEntityAttributeFlow.MAPPER
                    .mapToDatabaseEntity(programRuleAction.getTrackedEntityAttribute()));
            programRuleActionFlow.setDataElement(DataElementFlow.MAPPER
                    .mapToDatabaseEntity(programRuleAction.getDataElement()));
            programRuleActionFlow.setProgramIndicator(ProgramIndicatorFlow.MAPPER
                    .mapToDatabaseEntity(programRuleAction.getProgramIndicator()));
            programRuleActionFlow.setProgramStage(ProgramStageFlow.MAPPER
                    .mapToDatabaseEntity(programRuleAction.getProgramStage()));
            programRuleActionFlow.setProgramStageSection(ProgramStageSectionFlow.MAPPER
                    .mapToDatabaseEntity(programRuleAction.getProgramStageSection()));
            programRuleActionFlow.setProgramRuleActionType(
                    programRuleAction.getProgramRuleActionType());

            programRuleActionFlow.setContent(programRuleAction.getContent());
            programRuleActionFlow.setLocation(programRuleAction.getLocation());
            programRuleActionFlow.setData(programRuleAction.getData());

            return programRuleActionFlow;
        }

        @Override
        public ProgramRuleAction mapToModel(ProgramRuleActionFlow programRuleActionFlow) {
            if (programRuleActionFlow == null) {
                return null;
            }

            ProgramRuleAction programRuleAction = new ProgramRuleAction();
            programRuleAction.setId(programRuleActionFlow.getId());
            programRuleAction.setUId(programRuleActionFlow.getUId());
            programRuleAction.setCreated(programRuleActionFlow.getCreated());
            programRuleAction.setLastUpdated(programRuleActionFlow.getLastUpdated());
            programRuleAction.setAccess(programRuleActionFlow.getAccess());

            programRuleAction.setProgramRuleActionType(
                    programRuleActionFlow.getProgramRuleActionType());
            programRuleAction.setProgramRule(ProgramRuleFlow.MAPPER
                    .mapToModel(programRuleActionFlow.getProgramRule()));
            programRuleAction.setProgramStage(ProgramStageFlow.MAPPER
                    .mapToModel(programRuleActionFlow.getProgramStage()));
            programRuleAction.setProgramStageSection(ProgramStageSectionFlow.MAPPER
                    .mapToModel(programRuleActionFlow.getProgramStageSection()));
            programRuleAction.setProgramIndicator(ProgramIndicatorFlow.MAPPER
                    .mapToModel(programRuleActionFlow.getProgramIndicator()));
            programRuleAction.setTrackedEntityAttribute(TrackedEntityAttributeFlow.MAPPER
                    .mapToModel(programRuleActionFlow.getTrackedEntityAttribute()));
            programRuleAction.setDataElement(DataElementFlow.MAPPER
                    .mapToModel(programRuleActionFlow.getDataElement()));


            programRuleAction.setContent(programRuleActionFlow.getContent());
            programRuleAction.setLocation(programRuleActionFlow.getLocation());
            programRuleAction.setData(programRuleActionFlow.getData());

            return programRuleAction;
        }

        @Override
        public Class<ProgramRuleAction> getModelTypeClass() {
            return ProgramRuleAction.class;
        }

        @Override
        public Class<ProgramRuleActionFlow> getDatabaseEntityTypeClass() {
            return ProgramRuleActionFlow.class;
        }
    }
}
