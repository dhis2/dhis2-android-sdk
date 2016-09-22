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

import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.client.sdk.android.api.persistence.flow.TrackedEntityAttributeValueFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.TrackedEntityAttributeValueFlow_Table;
import org.hisp.dhis.client.sdk.android.common.AbsDataStore;
import org.hisp.dhis.client.sdk.android.common.Mapper;
import org.hisp.dhis.client.sdk.core.common.StateStore;
import org.hisp.dhis.client.sdk.core.common.persistence.IdentifiableObjectStore;
import org.hisp.dhis.client.sdk.core.program.ProgramTrackedEntityAttributeStore;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityAttributeValueStore;
import org.hisp.dhis.client.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityInstance;

import java.util.ArrayList;
import java.util.List;

public final class TrackedEntityAttributeValueStoreImpl extends
        AbsDataStore<TrackedEntityAttributeValue, TrackedEntityAttributeValueFlow>
        implements TrackedEntityAttributeValueStore {

    private final IdentifiableObjectStore<Program> programStore;
    private final ProgramTrackedEntityAttributeStore programTrackedEntityAttributeStore;
    public TrackedEntityAttributeValueStoreImpl(StateStore stateStore,
                                                IdentifiableObjectStore<Program> programStore,
                                                ProgramTrackedEntityAttributeStore programTrackedEntityAttributeStore) {
        super(TrackedEntityAttributeValueFlow.MAPPER, stateStore);
        this.programStore = programStore;
        this.programTrackedEntityAttributeStore = programTrackedEntityAttributeStore;
    }

    @Override
    public TrackedEntityAttributeValue query(TrackedEntityInstance trackedEntityInstance,
                                             TrackedEntityAttribute trackedEntityAttribute) {
        if (trackedEntityInstance == null || trackedEntityAttribute == null) {
            return null;
        }
        TrackedEntityAttributeValueFlow trackedEntityInstanceFlow = new Select()
                .from(TrackedEntityAttributeValueFlow.class)
                .where(TrackedEntityAttributeValueFlow_Table.trackedEntityInstance
                        .is(trackedEntityInstance.getTrackedEntityInstanceUid()))
                .and((TrackedEntityAttributeValueFlow_Table.trackedEntityAttributeUId)
                        .is(trackedEntityAttribute.getUId())).querySingle();
        return getMapper().mapToModel(trackedEntityInstanceFlow);
    }

    @Override
    public List<TrackedEntityAttributeValue> query(TrackedEntityInstance trackedEntityInstance) {
        if (trackedEntityInstance == null) {
            return null;
        }
        List<TrackedEntityAttributeValueFlow> trackedEntityInstanceFlows = new Select()
                .from(TrackedEntityAttributeValueFlow.class)
                .where(TrackedEntityAttributeValueFlow_Table.trackedEntityInstance
                        .is(trackedEntityInstance.getTrackedEntityInstanceUid()))
                .queryList();
        return getMapper().mapToModels(trackedEntityInstanceFlows);
    }

    @Override
    public List<TrackedEntityAttributeValue> query(Enrollment enrollment) {
        if (enrollment == null) {
            return null;
        }
        Program program = programStore.queryByUid(enrollment.getProgram());
        List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes =
                programTrackedEntityAttributeStore.query(program);
        if (programTrackedEntityAttributes == null) {
            return null;
        }
        List<TrackedEntityAttributeValueFlow> trackedEntityAttributeValueFlows = new ArrayList<>();
        for (ProgramTrackedEntityAttribute programTrackedEntityAttribute :
                programTrackedEntityAttributes) {
            List<TrackedEntityAttributeValueFlow> trackedEntityAttributeValueFlow = new Select()
                    .from(TrackedEntityAttributeValueFlow.class)
                    .where(TrackedEntityAttributeValueFlow_Table.trackedEntityInstance
                            .is(enrollment.getTrackedEntityInstance().getTrackedEntityInstanceUid
                                    ()))
                    .and(TrackedEntityAttributeValueFlow_Table.trackedEntityAttributeUId
                            .is(programTrackedEntityAttribute.getTrackedEntityAttribute().getUId()))
                    .queryList();
            if (trackedEntityAttributeValueFlow != null && !trackedEntityAttributeValueFlow.isEmpty()) {
                trackedEntityAttributeValueFlows.add(trackedEntityAttributeValueFlow.get(0));
            }
        }
        return getMapper().mapToModels(trackedEntityAttributeValueFlows);
    }
}
