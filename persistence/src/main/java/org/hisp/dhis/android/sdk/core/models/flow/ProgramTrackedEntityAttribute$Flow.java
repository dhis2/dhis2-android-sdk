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

package org.hisp.dhis.android.sdk.core.models.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.annotation.UniqueGroup;

import org.hisp.dhis.android.sdk.core.models.common.meta.DbDhis;
import org.hisp.dhis.android.sdk.models.program.ProgramTrackedEntityAttribute;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = DbDhis.NAME, uniqueColumnGroups = {
        @UniqueGroup(groupNumber = ProgramTrackedEntityAttribute$Flow.UNIQUE_TRACKED_ENTITY_PROGRAM, uniqueConflict = ConflictAction.FAIL)
})
public final class ProgramTrackedEntityAttribute$Flow extends BaseModel$Flow {
    static final int UNIQUE_TRACKED_ENTITY_PROGRAM = 1;

    @Column
    @Unique(unique = false, uniqueGroups = {UNIQUE_TRACKED_ENTITY_PROGRAM})
    String trackedEntityAttribute;

    @Column
    @Unique(unique = false, uniqueGroups = {UNIQUE_TRACKED_ENTITY_PROGRAM})
    String program;

    @Column
    int sortOrder;

    @Column
    boolean allowFutureDate;

    @Column
    boolean displayInList;

    @Column
    boolean mandatory;

    public String getTrackedEntityAttribute() {
        return trackedEntityAttribute;
    }

    public void setTrackedEntityAttribute(String trackedEntityAttribute) {
        this.trackedEntityAttribute = trackedEntityAttribute;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
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

    public ProgramTrackedEntityAttribute$Flow() {
        // empty constructor
    }

    public static ProgramTrackedEntityAttribute toModel(ProgramTrackedEntityAttribute$Flow programTrackedEntityAttributeFlow) {
        if (programTrackedEntityAttributeFlow == null) {
            return null;
        }

        ProgramTrackedEntityAttribute programTrackedEntityAttribute = new ProgramTrackedEntityAttribute();
        programTrackedEntityAttribute.setTrackedEntityAttribute(programTrackedEntityAttributeFlow.getTrackedEntityAttribute());
        programTrackedEntityAttribute.setProgram(programTrackedEntityAttributeFlow.getProgram());
        programTrackedEntityAttribute.setSortOrder(programTrackedEntityAttributeFlow.getSortOrder());
        programTrackedEntityAttribute.setAllowFutureDate(programTrackedEntityAttributeFlow.isAllowFutureDate());
        programTrackedEntityAttribute.setDisplayInList(programTrackedEntityAttributeFlow.isDisplayInList());
        programTrackedEntityAttribute.setMandatory(programTrackedEntityAttributeFlow.isMandatory());
        return programTrackedEntityAttribute;
    }

    public static ProgramTrackedEntityAttribute$Flow fromModel(ProgramTrackedEntityAttribute programTrackedEntityAttribute) {
        if (programTrackedEntityAttribute == null) {
            return null;
        }

        ProgramTrackedEntityAttribute$Flow programTrackedEntityAttributeFlow = new ProgramTrackedEntityAttribute$Flow();
        programTrackedEntityAttributeFlow.setTrackedEntityAttribute(programTrackedEntityAttribute.getTrackedEntityAttribute());
        programTrackedEntityAttributeFlow.setProgram(programTrackedEntityAttribute.getProgram());
        programTrackedEntityAttributeFlow.setSortOrder(programTrackedEntityAttribute.getSortOrder());
        programTrackedEntityAttributeFlow.setAllowFutureDate(programTrackedEntityAttribute.isAllowFutureDate());
        programTrackedEntityAttributeFlow.setDisplayInList(programTrackedEntityAttribute.isDisplayInList());
        programTrackedEntityAttributeFlow.setMandatory(programTrackedEntityAttribute.isMandatory());
        return programTrackedEntityAttributeFlow;
    }

    public static List<ProgramTrackedEntityAttribute> toModels(List<ProgramTrackedEntityAttribute$Flow> programTrackedEntityAttributeFlows) {
        List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes = new ArrayList<>();

        if (programTrackedEntityAttributeFlows != null && !programTrackedEntityAttributeFlows.isEmpty()) {
            for (ProgramTrackedEntityAttribute$Flow programTrackedEntityAttributeFlow : programTrackedEntityAttributeFlows) {
                programTrackedEntityAttributes.add(toModel(programTrackedEntityAttributeFlow));
            }
        }

        return programTrackedEntityAttributes;
    }

    public static List<ProgramTrackedEntityAttribute$Flow> fromModels(List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes) {
        List<ProgramTrackedEntityAttribute$Flow> programTrackedEntityAttributeFlows = new ArrayList<>();

        if (programTrackedEntityAttributes != null && !programTrackedEntityAttributes.isEmpty()) {
            for (ProgramTrackedEntityAttribute programTrackedEntityAttribute : programTrackedEntityAttributes) {
                programTrackedEntityAttributeFlows.add(fromModel(programTrackedEntityAttribute));
            }
        }

        return programTrackedEntityAttributeFlows;
    }
}
