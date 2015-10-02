/*
 * Copyright (c) 2015, University of Oslo
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

package org.hisp.dhis.android.sdk.core.persistence.models.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.android.sdk.core.persistence.models.common.meta.DbDhis;
import org.hisp.dhis.android.sdk.models.trackedentity.TrackedEntityAttribute;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class TrackedEntityAttribute$Flow extends BaseIdentifiableObject$Flow {

    @Column
    String optionSet;

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
    String valueType;

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

    public String getOptionSet() {
        return optionSet;
    }

    public void setOptionSet(String optionSet) {
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

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
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

    public TrackedEntityAttribute$Flow() {
        // empty constructor
    }

    public static TrackedEntityAttribute toModel(TrackedEntityAttribute$Flow trackedEntityAttributeFlow) {
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
        trackedEntityAttribute.setOptionSet(trackedEntityAttributeFlow.getOptionSet());
        trackedEntityAttribute.setUnique(trackedEntityAttributeFlow.isUnique());
        trackedEntityAttribute.setProgramScope(trackedEntityAttributeFlow.isProgramScope());
        trackedEntityAttribute.setOrgunitScope(trackedEntityAttributeFlow.isOrgunitScope());
        trackedEntityAttribute.setDisplayInListNoProgram(trackedEntityAttributeFlow.isDisplayInListNoProgram());
        trackedEntityAttribute.setDisplayOnVisitSchedule(trackedEntityAttributeFlow.isDisplayOnVisitSchedule());
        trackedEntityAttribute.setExternalAccess(trackedEntityAttributeFlow.isExternalAccess());
        trackedEntityAttribute.setValueType(trackedEntityAttributeFlow.getValueType());
        trackedEntityAttribute.setConfidential(trackedEntityAttributeFlow.isConfidential());
        trackedEntityAttribute.setInherit(trackedEntityAttributeFlow.isInherit());
        trackedEntityAttribute.setSortOrderVisitSchedule(trackedEntityAttributeFlow.getSortOrderVisitSchedule());
        trackedEntityAttribute.setDimension(trackedEntityAttributeFlow.getDimension());
        trackedEntityAttribute.setSortOrderInListNoProgram(trackedEntityAttributeFlow.getSortOrderInListNoProgram());
        return trackedEntityAttribute;
    }

    public static TrackedEntityAttribute$Flow fromModel(TrackedEntityAttribute trackedEntityAttribute) {
        if (trackedEntityAttribute == null) {
            return null;
        }

        TrackedEntityAttribute$Flow trackedEntityAttributeFlow = new TrackedEntityAttribute$Flow();
        trackedEntityAttributeFlow.setId(trackedEntityAttribute.getId());
        trackedEntityAttributeFlow.setUId(trackedEntityAttribute.getUId());
        trackedEntityAttributeFlow.setCreated(trackedEntityAttribute.getCreated());
        trackedEntityAttributeFlow.setLastUpdated(trackedEntityAttribute.getLastUpdated());
        trackedEntityAttributeFlow.setName(trackedEntityAttribute.getName());
        trackedEntityAttributeFlow.setDisplayName(trackedEntityAttribute.getDisplayName());
        trackedEntityAttributeFlow.setAccess(trackedEntityAttribute.getAccess());
        trackedEntityAttributeFlow.setOptionSet(trackedEntityAttribute.getOptionSet());
        trackedEntityAttributeFlow.setUnique(trackedEntityAttribute.isUnique());
        trackedEntityAttributeFlow.setProgramScope(trackedEntityAttribute.isProgramScope());
        trackedEntityAttributeFlow.setOrgunitScope(trackedEntityAttribute.isOrgunitScope());
        trackedEntityAttributeFlow.setDisplayInListNoProgram(trackedEntityAttribute.isDisplayInListNoProgram());
        trackedEntityAttributeFlow.setDisplayOnVisitSchedule(trackedEntityAttribute.isDisplayOnVisitSchedule());
        trackedEntityAttributeFlow.setExternalAccess(trackedEntityAttribute.isExternalAccess());
        trackedEntityAttributeFlow.setValueType(trackedEntityAttribute.getValueType());
        trackedEntityAttributeFlow.setConfidential(trackedEntityAttribute.isConfidential());
        trackedEntityAttributeFlow.setInherit(trackedEntityAttribute.isInherit());
        trackedEntityAttributeFlow.setSortOrderVisitSchedule(trackedEntityAttribute.getSortOrderVisitSchedule());
        trackedEntityAttributeFlow.setDimension(trackedEntityAttribute.getDimension());
        trackedEntityAttributeFlow.setSortOrderInListNoProgram(trackedEntityAttribute.getSortOrderInListNoProgram());
        return trackedEntityAttributeFlow;
    }

    public static List<TrackedEntityAttribute> toModels(List<TrackedEntityAttribute$Flow> trackedEntityAttributeFlows) {
        List<TrackedEntityAttribute> trackedEntityAttributes = new ArrayList<>();

        if (trackedEntityAttributeFlows != null && !trackedEntityAttributeFlows.isEmpty()) {
            for (TrackedEntityAttribute$Flow trackedEntityAttributeFlow : trackedEntityAttributeFlows) {
                trackedEntityAttributes.add(toModel(trackedEntityAttributeFlow));
            }
        }

        return trackedEntityAttributes;
    }

    public static List<TrackedEntityAttribute$Flow> fromModels(List<TrackedEntityAttribute> trackedEntityAttributes) {
        List<TrackedEntityAttribute$Flow> trackedEntityAttributeFlows = new ArrayList<>();

        if (trackedEntityAttributes != null && !trackedEntityAttributes.isEmpty()) {
            for (TrackedEntityAttribute trackedEntityAttribute : trackedEntityAttributes) {
                trackedEntityAttributeFlows.add(fromModel(trackedEntityAttribute));
            }
        }

        return trackedEntityAttributeFlows;
    }
}
