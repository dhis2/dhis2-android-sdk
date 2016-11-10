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
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.UniqueGroup;

import org.hisp.dhis.client.sdk.android.api.persistence.DbDhis;
import org.hisp.dhis.client.sdk.android.common.AbsMapper;
import org.hisp.dhis.client.sdk.android.common.Mapper;
import org.hisp.dhis.client.sdk.models.program.ProgramStageDataElement;

@Table(database = DbDhis.class, uniqueColumnGroups = {
        @UniqueGroup(
                groupNumber = ProgramStageDataElementFlow.UNIQUE_PROGRAM_DATA_ELEMENT_GROUP,
                uniqueConflict = ConflictAction.FAIL)
})
public final class ProgramStageDataElementFlow extends BaseIdentifiableObjectFlow {
    public static Mapper<ProgramStageDataElement, ProgramStageDataElementFlow>
            MAPPER = new ProgramStageDataElementMapper();

    static final int UNIQUE_PROGRAM_DATA_ELEMENT_GROUP = 1;
    static final String PROGRAM_STAGE_KEY = "programStage";
    static final String DATA_ELEMENT_KEY = "dataElement";
    static final String PROGRAM_STAGE_SECTION_KEY = "programStageSection";

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(
                            columnName = PROGRAM_STAGE_KEY, columnType = String.class,
                            foreignKeyColumnName = BaseIdentifiableObjectFlow.COLUMN_UID),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
    ProgramStageFlow programStage;

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(
                            columnName = PROGRAM_STAGE_SECTION_KEY, columnType = String.class,
                            foreignKeyColumnName = BaseIdentifiableObjectFlow.COLUMN_UID),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
    ProgramStageSectionFlow programStageSection;

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(
                            columnName = DATA_ELEMENT_KEY, columnType = String.class,
                            foreignKeyColumnName = BaseIdentifiableObjectFlow.COLUMN_UID),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
    DataElementFlow dataElement;

    @Column
    boolean allowFutureDate;

    @Column
    int sortOrder;

    @Column
    boolean displayInReports;

    @Column
    boolean allowProvidedElsewhere;

    @Column
    boolean compulsory;

    @Column
    int sortOrderWithinProgramStageSection;

    public ProgramStageDataElementFlow() {
        // empty constructor
    }

    public ProgramStageFlow getProgramStage() {
        return programStage;
    }

    public void setProgramStage(ProgramStageFlow programStage) {
        this.programStage = programStage;
    }

    public DataElementFlow getDataElement() {
        return dataElement;
    }

    public void setDataElement(DataElementFlow dataElement) {
        this.dataElement = dataElement;
    }

    public boolean isAllowFutureDate() {
        return allowFutureDate;
    }

    public void setAllowFutureDate(boolean allowFutureDate) {
        this.allowFutureDate = allowFutureDate;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isDisplayInReports() {
        return displayInReports;
    }

    public void setDisplayInReports(boolean displayInReports) {
        this.displayInReports = displayInReports;
    }

    public boolean isAllowProvidedElsewhere() {
        return allowProvidedElsewhere;
    }

    public void setAllowProvidedElsewhere(boolean allowProvidedElsewhere) {
        this.allowProvidedElsewhere = allowProvidedElsewhere;
    }

    public boolean isCompulsory() {
        return compulsory;
    }

    public void setCompulsory(boolean compulsory) {
        this.compulsory = compulsory;
    }

    public ProgramStageSectionFlow getProgramStageSection() {
        return programStageSection;
    }

    public void setProgramStageSection(ProgramStageSectionFlow programStageSection) {
        this.programStageSection = programStageSection;
    }

    public int getSortOrderWithinProgramStageSection() {
        return sortOrderWithinProgramStageSection;
    }

    public void setSortOrderWithinProgramStageSection(int sortOrderWithinProgramStageSection) {
        this.sortOrderWithinProgramStageSection = sortOrderWithinProgramStageSection;
    }

    private static class ProgramStageDataElementMapper extends AbsMapper<ProgramStageDataElement,
            ProgramStageDataElementFlow> {

        @Override
        public ProgramStageDataElementFlow mapToDatabaseEntity(ProgramStageDataElement model) {
            if (model == null) {
                return null;
            }

            ProgramStageDataElementFlow flow = new ProgramStageDataElementFlow();
            flow.setId(model.getId());
            flow.setUId(model.getUId());
            flow.setCreated(model.getCreated());
            flow.setLastUpdated(model.getLastUpdated());
            flow.setName(model.getName());
            flow.setDisplayName(model.getDisplayName());
            flow.setAccess(model.getAccess());
            flow.setAllowFutureDate(model.isAllowFutureDate());
            flow.setSortOrder(model.getSortOrder());
            flow.setDisplayInReports(model.isDisplayInReports());
            flow.setAllowProvidedElsewhere(model.isAllowProvidedElsewhere());
            flow.setCompulsory(model.isCompulsory());
            flow.setSortOrderWithinProgramStageSection(model.getSortOrderWithinProgramStageSection());

            flow.setProgramStage(ProgramStageFlow.MAPPER
                    .mapToDatabaseEntity(model.getProgramStage()));
            flow.setProgramStageSection(ProgramStageSectionFlow.MAPPER
                    .mapToDatabaseEntity(model.getProgramStageSection()));
            flow.setDataElement(DataElementFlow.MAPPER
                    .mapToDatabaseEntity(model.getDataElement()));

            return flow;
        }

        @Override
        public ProgramStageDataElement mapToModel(ProgramStageDataElementFlow flow) {
            if (flow == null) {
                return null;
            }

            ProgramStageDataElement model = new ProgramStageDataElement();
            model.setId(flow.getId());
            model.setUId(flow.getUId());
            model.setCreated(flow.getCreated());
            model.setLastUpdated(flow.getLastUpdated());
            model.setName(flow.getName());
            model.setDisplayName(flow.getDisplayName());
            model.setAccess(flow.getAccess());
            model.setAllowFutureDate(flow.isAllowFutureDate());
            model.setSortOrder(flow.getSortOrder());
            model.setDisplayInReports(flow.isDisplayInReports());
            model.setAllowProvidedElsewhere(flow.isAllowProvidedElsewhere());
            model.setCompulsory(flow.isCompulsory());
            model.setSortOrderWithinProgramStageSection(flow.getSortOrderWithinProgramStageSection());

            model.setProgramStage(ProgramStageFlow.MAPPER
                    .mapToModel(flow.getProgramStage()));
            model.setProgramStageSection(ProgramStageSectionFlow.MAPPER
                    .mapToModel(flow.getProgramStageSection()));
            model.setDataElement(DataElementFlow.MAPPER
                    .mapToModel(flow.getDataElement()));

            return model;
        }

        @Override
        public Class<ProgramStageDataElement> getModelTypeClass() {
            return ProgramStageDataElement.class;
        }

        @Override
        public Class<ProgramStageDataElementFlow> getDatabaseEntityTypeClass() {
            return ProgramStageDataElementFlow.class;
        }
    }
}
