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

package org.hisp.dhis.client.sdk.android.trackedentity;

import org.hisp.dhis.client.sdk.android.api.modules.MapperModule;
import org.hisp.dhis.client.sdk.android.common.base.AbsMapper;
import org.hisp.dhis.client.sdk.android.common.base.IMapper;
import org.hisp.dhis.client.sdk.android.flow.OptionSet$Flow;
import org.hisp.dhis.client.sdk.android.flow.TrackedEntityAttribute$Flow;
import org.hisp.dhis.client.sdk.android.optionset.OptionSetMapper;
import org.hisp.dhis.client.sdk.models.optionset.OptionSet;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttribute;

public class TrackedEntityAttributeMapper extends AbsMapper<TrackedEntityAttribute,
        TrackedEntityAttribute$Flow> {

    @Override
    public TrackedEntityAttribute$Flow mapToDatabaseEntity(TrackedEntityAttribute trackedEntityAttribute) {
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
        trackedEntityAttributeFlow.setOptionSet(MapperModule.getInstance().getOptionSetMapper().mapToDatabaseEntity(trackedEntityAttribute.getOptionSet()));
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

    @Override
    public TrackedEntityAttribute mapToModel(TrackedEntityAttribute$Flow trackedEntityAttributeFlow) {
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
        trackedEntityAttribute.setOptionSet(MapperModule.getInstance().getOptionSetMapper().mapToModel(trackedEntityAttributeFlow.getOptionSet()));
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

    @Override
    public Class<TrackedEntityAttribute> getModelTypeClass() {
        return TrackedEntityAttribute.class;
    }

    @Override
    public Class<TrackedEntityAttribute$Flow> getDatabaseEntityTypeClass() {
        return TrackedEntityAttribute$Flow.class;
    }
}
