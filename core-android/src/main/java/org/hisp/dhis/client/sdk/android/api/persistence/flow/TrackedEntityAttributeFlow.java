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
import org.hisp.dhis.client.sdk.models.dataelement.ValueType;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttribute;

@Table(database = DbDhis.class)
public final class TrackedEntityAttributeFlow extends BaseIdentifiableObjectFlow {
    public static final Mapper<TrackedEntityAttribute, TrackedEntityAttributeFlow>
            MAPPER = new AttributeMapper();

    final static String OPTION_SET_KEY = "optionSet";

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(
                            columnName = OPTION_SET_KEY, columnType = String.class,
                            foreignKeyColumnName = BaseIdentifiableObjectFlow.COLUMN_UID),
            }, saveForeignKeyModel = true, onDelete = ForeignKeyAction.NO_ACTION
    )
    OptionSetFlow optionSet;

    @Column
    boolean isUnique;

    @Column
    boolean programScope;

    @Column
    boolean orgunitScope;

    @Column
    boolean displayInListNoProgram;

    @Column
    boolean displayOnVisitSchedule;

    @Column
    boolean externalAccess;

    @Column
    ValueType valueType;

    @Column
    boolean confidential;

    @Column
    boolean inherit;

    @Column
    int sortOrderVisitSchedule;

    @Column
    String dimension;

    @Column
    int sortOrderInListNoProgram;

    public TrackedEntityAttributeFlow() {
        // empty constructor
    }

    public OptionSetFlow getOptionSet() {
        return optionSet;
    }

    public void setOptionSet(OptionSetFlow optionSet) {
        this.optionSet = optionSet;
    }

    public boolean isUnique() {
        return isUnique;
    }

    public void setUnique(boolean isUnique) {
        this.isUnique = isUnique;
    }

    public boolean isProgramScope() {
        return programScope;
    }

    public void setProgramScope(boolean programScope) {
        this.programScope = programScope;
    }

    public boolean isOrgunitScope() {
        return orgunitScope;
    }

    public void setOrgunitScope(boolean orgunitScope) {
        this.orgunitScope = orgunitScope;
    }

    public boolean isDisplayInListNoProgram() {
        return displayInListNoProgram;
    }

    public void setDisplayInListNoProgram(boolean displayInListNoProgram) {
        this.displayInListNoProgram = displayInListNoProgram;
    }

    public boolean isDisplayOnVisitSchedule() {
        return displayOnVisitSchedule;
    }

    public void setDisplayOnVisitSchedule(boolean displayOnVisitSchedule) {
        this.displayOnVisitSchedule = displayOnVisitSchedule;
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

    public boolean isConfidential() {
        return confidential;
    }

    public void setConfidential(boolean confidential) {
        this.confidential = confidential;
    }

    public boolean isInherit() {
        return inherit;
    }

    public void setInherit(boolean inherit) {
        this.inherit = inherit;
    }

    public int getSortOrderVisitSchedule() {
        return sortOrderVisitSchedule;
    }

    public void setSortOrderVisitSchedule(int sortOrderVisitSchedule) {
        this.sortOrderVisitSchedule = sortOrderVisitSchedule;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public int getSortOrderInListNoProgram() {
        return sortOrderInListNoProgram;
    }

    public void setSortOrderInListNoProgram(int sortOrderInListNoProgram) {
        this.sortOrderInListNoProgram = sortOrderInListNoProgram;
    }

    private static class AttributeMapper extends
            AbsMapper<TrackedEntityAttribute, TrackedEntityAttributeFlow> {

        @Override
        public TrackedEntityAttributeFlow mapToDatabaseEntity(TrackedEntityAttribute attribute) {
            if (attribute == null) {
                return null;
            }

            TrackedEntityAttributeFlow attributeFlow =
                    new TrackedEntityAttributeFlow();
            attributeFlow.setId(attribute.getId());
            attributeFlow.setUId(attribute.getUId());
            attributeFlow.setCreated(attribute.getCreated());
            attributeFlow.setLastUpdated(attribute.getLastUpdated());
            attributeFlow.setName(attribute.getName());
            attributeFlow.setDisplayName(attribute.getDisplayName());
            attributeFlow.setAccess(attribute.getAccess());
            attributeFlow.setExternalAccess(attribute.isExternalAccess());
            attributeFlow.setUnique(attribute.isUnique());
            attributeFlow.setConfidential(attribute.isConfidential());
            attributeFlow.setDimension(attribute.getDimension());
            attributeFlow.setDisplayInListNoProgram(attribute.isDisplayInListNoProgram());
            attributeFlow.setDisplayOnVisitSchedule(attribute.isDisplayOnVisitSchedule());
            attributeFlow.setInherit(attribute.isInherit());
            attributeFlow.setOrgunitScope(attribute.isOrgunitScope());
            attributeFlow.setProgramScope(attribute.isProgramScope());
            attributeFlow.setSortOrderInListNoProgram(attribute.getSortOrderInListNoProgram());
            attributeFlow.setSortOrderVisitSchedule(attribute.getSortOrderVisitSchedule());
            attributeFlow.setValueType(attribute.getValueType());
            attributeFlow.setOptionSet(OptionSetFlow.MAPPER
                    .mapToDatabaseEntity(attribute.getOptionSet()));
            return attributeFlow;
        }

        @Override
        public TrackedEntityAttribute mapToModel(TrackedEntityAttributeFlow attributeFlow) {
            if (attributeFlow == null) {
                return null;
            }

            TrackedEntityAttribute attribute = new TrackedEntityAttribute();
            attribute.setId(attributeFlow.getId());
            attribute.setUId(attributeFlow.getUId());
            attribute.setCreated(attributeFlow.getCreated());
            attribute.setLastUpdated(attributeFlow.getLastUpdated());
            attribute.setName(attributeFlow.getName());
            attribute.setDisplayName(attributeFlow.getDisplayName());
            attribute.setAccess(attributeFlow.getAccess());
            attribute.setExternalAccess(attributeFlow.isExternalAccess());
            attribute.setUnique(attributeFlow.isUnique());
            attribute.setConfidential(attributeFlow.isConfidential());
            attribute.setDimension(attributeFlow.getDimension());
            attribute.setDisplayInListNoProgram(attributeFlow.isDisplayInListNoProgram());
            attribute.setDisplayOnVisitSchedule(attributeFlow.isDisplayOnVisitSchedule());
            attribute.setInherit(attributeFlow.isInherit());
            attribute.setOrgunitScope(attributeFlow.isOrgunitScope());
            attribute.setProgramScope(attributeFlow.isProgramScope());
            attribute.setSortOrderInListNoProgram(attributeFlow.getSortOrderInListNoProgram());
            attribute.setSortOrderVisitSchedule(attributeFlow.getSortOrderVisitSchedule());
            attribute.setValueType(attributeFlow.getValueType());
            attribute.setOptionSet(OptionSetFlow.MAPPER.mapToModel(attributeFlow.getOptionSet()));
            return attribute;
        }

        @Override
        public Class<TrackedEntityAttribute> getModelTypeClass() {
            return TrackedEntityAttribute.class;
        }

        @Override
        public Class<TrackedEntityAttributeFlow> getDatabaseEntityTypeClass() {
            return TrackedEntityAttributeFlow.class;
        }
    }
}
