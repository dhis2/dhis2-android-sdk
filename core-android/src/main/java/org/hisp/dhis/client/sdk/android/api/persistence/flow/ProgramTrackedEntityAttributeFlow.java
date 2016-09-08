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
import org.hisp.dhis.client.sdk.models.program.ProgramTrackedEntityAttribute;

@Table(database = DbDhis.class, uniqueColumnGroups = {
        @UniqueGroup(
                groupNumber = ProgramTrackedEntityAttributeFlow.UNIQUE_TRACKED_ENTITY_PROGRAM,
                uniqueConflict = ConflictAction.FAIL)
})
public final class ProgramTrackedEntityAttributeFlow extends BaseIdentifiableObjectFlow {
    public static final Mapper<ProgramTrackedEntityAttribute, ProgramTrackedEntityAttributeFlow>
            MAPPER = new AttributeMapper();

    static final int UNIQUE_TRACKED_ENTITY_PROGRAM = 1;
    static final String TRACKED_ENTITY_ATTRIBUTE_KEY = "trackedEntityAttribute";
    static final String PROGRAM_KEY = "program";

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(
                            columnName = TRACKED_ENTITY_ATTRIBUTE_KEY, columnType = String.class,
                            foreignKeyColumnName = BaseIdentifiableObjectFlow.COLUMN_UID),
            }, saveForeignKeyModel = true, onDelete = ForeignKeyAction.NO_ACTION
    )
    TrackedEntityAttributeFlow trackedEntityAttribute;

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(
                            columnName = PROGRAM_KEY, columnType = String.class,
                            foreignKeyColumnName = BaseIdentifiableObjectFlow.COLUMN_UID),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
    ProgramFlow program;

    @Column
    boolean allowFutureDate;

    @Column
    boolean displayInList;

    @Column
    boolean mandatory;



    public ProgramTrackedEntityAttributeFlow() {
        // empty constructor
    }

    public TrackedEntityAttributeFlow getTrackedEntityAttribute() {
        return trackedEntityAttribute;
    }

    public void setTrackedEntityAttribute(TrackedEntityAttributeFlow trackedEntityAttribute) {
        this.trackedEntityAttribute = trackedEntityAttribute;
    }

    public ProgramFlow getProgram() {
        return program;
    }

    public void setProgram(ProgramFlow program) {
        this.program = program;
    }

    public int getSortOrder() {
        return apiSortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.apiSortOrder = sortOrder;
    }

    public boolean isAllowFutureDate() {
        return allowFutureDate;
    }

    public void setAllowFutureDate(boolean allowFutureDate) {
        this.allowFutureDate = allowFutureDate;
    }

    public boolean isDisplayInList() {
        return displayInList;
    }

    public void setDisplayInList(boolean displayInList) {
        this.displayInList = displayInList;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    private static class AttributeMapper
            extends AbsMapper<ProgramTrackedEntityAttribute, ProgramTrackedEntityAttributeFlow> {

        @Override
        public ProgramTrackedEntityAttributeFlow mapToDatabaseEntity(
                ProgramTrackedEntityAttribute attribute) {
            if (attribute == null) {
                return null;
            }

            ProgramTrackedEntityAttributeFlow attributeFlow =
                    new ProgramTrackedEntityAttributeFlow();
            attributeFlow.setId(attribute.getId());
            attributeFlow.setUId(attribute.getUId());
            attributeFlow.setCreated(attribute.getCreated());
            attributeFlow.setLastUpdated(attribute.getLastUpdated());
            attributeFlow.setName(attribute.getName());
            attributeFlow.setDisplayName(attribute.getDisplayName());
            attributeFlow.setAccess(attribute.getAccess());
            attributeFlow.setTrackedEntityAttribute(TrackedEntityAttributeFlow.MAPPER
                    .mapToDatabaseEntity(attribute.getTrackedEntityAttribute()));
            attributeFlow.setProgram(ProgramFlow.MAPPER
                    .mapToDatabaseEntity(attribute.getProgram()));
            attributeFlow.setSortOrder(attribute.getApiSortOrder());
            attributeFlow.setAllowFutureDate(attribute.isAllowFutureDate());
            attributeFlow.setDisplayInList(attribute.isDisplayInList());
            attributeFlow.setMandatory(attribute.isMandatory());
            return attributeFlow;
        }

        @Override
        public ProgramTrackedEntityAttribute mapToModel(
                ProgramTrackedEntityAttributeFlow attributeFlow) {

            if (attributeFlow == null) {
                return null;
            }

            ProgramTrackedEntityAttribute attribute = new ProgramTrackedEntityAttribute();
            attribute.setId(attributeFlow.getId());
            attribute.setUId(attributeFlow.getUId());
            attribute.setCreated(attributeFlow.getCreated());
            attribute.setLastUpdated(attributeFlow.getLastUpdated());
            attribute.setName(attributeFlow.getName());
            attribute.setDisplayName(attributeFlow.getDisplayName());
            attribute.setAccess(attributeFlow.getAccess());
            attribute.setTrackedEntityAttribute(TrackedEntityAttributeFlow.MAPPER
                    .mapToModel(attributeFlow.getTrackedEntityAttribute()));
            attribute.setProgram(ProgramFlow.MAPPER
                    .mapToModel(attributeFlow.getProgram()));
            attribute.setApiSortOrder(attributeFlow.getSortOrder());
            attribute.setAllowFutureDate(attributeFlow.isAllowFutureDate());
            attribute.setDisplayInList(attributeFlow.isDisplayInList());
            attribute.setMandatory(attributeFlow.isMandatory());
            return attribute;
        }

        @Override
        public Class<ProgramTrackedEntityAttribute> getModelTypeClass() {
            return ProgramTrackedEntityAttribute.class;
        }

        @Override
        public Class<ProgramTrackedEntityAttributeFlow> getDatabaseEntityTypeClass() {
            return ProgramTrackedEntityAttributeFlow.class;
        }
    }
}
