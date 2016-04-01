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
import org.hisp.dhis.client.sdk.models.common.ValueType;
import org.hisp.dhis.client.sdk.models.program.ProgramStageSection;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttribute;

@Table(database = DbDhis.class)
public final class TrackedEntityAttributeFlow extends BaseIdentifiableObjectFlow {

    public static final IMapper<TrackedEntityAttribute, TrackedEntityAttributeFlow> MAPPER = new Mapper();

    final static String OPTION_SET_KEY = "optionset";

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = OPTION_SET_KEY,
                            columnType = String.class, foreignKeyColumnName = "uId"),
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

    private static class Mapper extends AbsMapper<TrackedEntityAttribute, TrackedEntityAttributeFlow> {

        @Override
        public TrackedEntityAttributeFlow mapToDatabaseEntity(TrackedEntityAttribute trackedEntityAttribute) {
            if (trackedEntityAttribute == null) {
                return null;
            }

            TrackedEntityAttributeFlow trackedEntityAttributeFlow = new TrackedEntityAttributeFlow();
            trackedEntityAttributeFlow.setId(trackedEntityAttribute.getId());
            trackedEntityAttributeFlow.setUId(trackedEntityAttribute.getUId());
            trackedEntityAttributeFlow.setCreated(trackedEntityAttribute.getCreated());
            trackedEntityAttributeFlow.setLastUpdated(trackedEntityAttribute.getLastUpdated());
            trackedEntityAttributeFlow.setName(trackedEntityAttribute.getName());
            trackedEntityAttributeFlow.setDisplayName(trackedEntityAttribute.getDisplayName());
            trackedEntityAttributeFlow.setAccess(trackedEntityAttribute.getAccess());
            trackedEntityAttributeFlow.setExternalAccess(trackedEntityAttribute.isExternalAccess());
            trackedEntityAttributeFlow.setUnique(trackedEntityAttribute.isUnique());
            trackedEntityAttributeFlow.setConfidential(trackedEntityAttribute.isConfidential());
            trackedEntityAttributeFlow.setDimension(trackedEntityAttribute.getDimension());
            trackedEntityAttributeFlow.setDisplayInListNoProgram(trackedEntityAttribute.isDisplayInListNoProgram());
            trackedEntityAttributeFlow.setDisplayOnVisitSchedule(trackedEntityAttribute.isDisplayOnVisitSchedule());
            trackedEntityAttributeFlow.setInherit(trackedEntityAttribute.isInherit());
            trackedEntityAttributeFlow.setOrgunitScope(trackedEntityAttribute.isOrgunitScope());
            trackedEntityAttributeFlow.setProgramScope(trackedEntityAttribute.isProgramScope());
            trackedEntityAttributeFlow.setSortOrderInListNoProgram(trackedEntityAttribute.getSortOrderInListNoProgram());
            trackedEntityAttributeFlow.setSortOrderVisitSchedule(trackedEntityAttribute.getSortOrderVisitSchedule());
            trackedEntityAttributeFlow.setValueType(trackedEntityAttribute.getValueType());
            trackedEntityAttributeFlow.setOptionSet(OptionSetFlow.MAPPER
                    .mapToDatabaseEntity(trackedEntityAttribute.getOptionSet()));
            return trackedEntityAttributeFlow;
        }

        @Override
        public TrackedEntityAttribute mapToModel(TrackedEntityAttributeFlow trackedEntityAttributeFlow) {
            if (trackedEntityAttributeFlow == null) {
                return null;
            }


            TrackedEntityAttribute trackedEntityAttribute = new TrackedEntityAttribute();
            trackedEntityAttribute.setId(trackedEntityAttributeFlow.getId());
            trackedEntityAttribute.setUId(trackedEntityAttributeFlow.getUId());
            trackedEntityAttribute.setCreated(trackedEntityAttributeFlow.getCreated());
            trackedEntityAttribute.setLastUpdated(trackedEntityAttributeFlow.getLastUpdated());
            trackedEntityAttribute.setName(trackedEntityAttributeFlow.getName());
            trackedEntityAttribute.setDisplayName(trackedEntityAttributeFlow.getDisplayName());
            trackedEntityAttribute.setAccess(trackedEntityAttributeFlow.getAccess());
            trackedEntityAttribute.setExternalAccess(trackedEntityAttributeFlow.isExternalAccess());
            trackedEntityAttribute.setUnique(trackedEntityAttributeFlow.isUnique());
            trackedEntityAttribute.setConfidential(trackedEntityAttributeFlow.isConfidential());
            trackedEntityAttribute.setDimension(trackedEntityAttributeFlow.getDimension());
            trackedEntityAttribute.setDisplayInListNoProgram(trackedEntityAttributeFlow.isDisplayInListNoProgram());
            trackedEntityAttribute.setDisplayOnVisitSchedule(trackedEntityAttributeFlow.isDisplayOnVisitSchedule());
            trackedEntityAttribute.setInherit(trackedEntityAttributeFlow.isInherit());
            trackedEntityAttribute.setOrgunitScope(trackedEntityAttributeFlow.isOrgunitScope());
            trackedEntityAttribute.setProgramScope(trackedEntityAttributeFlow.isProgramScope());
            trackedEntityAttribute.setSortOrderInListNoProgram(trackedEntityAttributeFlow.getSortOrderInListNoProgram());
            trackedEntityAttribute.setSortOrderVisitSchedule(trackedEntityAttributeFlow.getSortOrderVisitSchedule());
            trackedEntityAttribute.setValueType(trackedEntityAttributeFlow.getValueType());
            trackedEntityAttribute.setOptionSet(OptionSetFlow.MAPPER
                    .mapToModel(trackedEntityAttributeFlow.getOptionSet()));
            return trackedEntityAttribute;
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
