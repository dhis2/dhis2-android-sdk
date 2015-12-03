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

package org.hisp.dhis.android.sdk.trackedentity;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.flow.TrackedEntityAttributeValue$Flow;
import org.hisp.dhis.android.sdk.flow.TrackedEntityAttributeValue$Flow$Table;
import org.hisp.dhis.java.sdk.common.persistence.IIdentifiableObjectStore;
import org.hisp.dhis.java.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.java.sdk.models.program.Program;
import org.hisp.dhis.java.sdk.models.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.java.sdk.trackedentity.ITrackedEntityAttributeValueStore;
import org.hisp.dhis.java.sdk.models.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.java.sdk.models.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.java.sdk.models.trackedentity.TrackedEntityInstance;

import java.util.ArrayList;
import java.util.List;

public final class TrackedEntityAttributeValueStore implements ITrackedEntityAttributeValueStore {

    private final IIdentifiableObjectStore<Program> programStore;

    public TrackedEntityAttributeValueStore(IIdentifiableObjectStore<Program> programStore) {
        this.programStore = programStore;
    }

    @Override
    public boolean insert(TrackedEntityAttributeValue object) {
        TrackedEntityAttributeValue$Flow trackedEntityAttributeValueFlow =
                TrackedEntityAttributeValue$Flow.fromModel(object);
        trackedEntityAttributeValueFlow.insert();

        return true;
    }

    @Override
    public boolean update(TrackedEntityAttributeValue object) {
        TrackedEntityAttributeValue$Flow.fromModel(object).update();
        return true;
    }

    @Override
    public boolean save(TrackedEntityAttributeValue object) {
        TrackedEntityAttributeValue$Flow trackedEntityAttributeValueFlow =
                TrackedEntityAttributeValue$Flow.fromModel(object);
        trackedEntityAttributeValueFlow.save();
        return true;
    }

    @Override
    public boolean delete(TrackedEntityAttributeValue object) {
        TrackedEntityAttributeValue$Flow.fromModel(object).delete();
        return true;
    }

    @Override
    public TrackedEntityAttributeValue queryById(long id) {
        return null;
    }

    @Override
    public List<TrackedEntityAttributeValue> queryAll() {
        List<TrackedEntityAttributeValue$Flow> trackedEntityAttributeValueFlow = new Select()
                .from(TrackedEntityAttributeValue$Flow.class)
                .queryList();
        return TrackedEntityAttributeValue$Flow.toModels(trackedEntityAttributeValueFlow);
    }

    @Override
    public TrackedEntityAttributeValue query(TrackedEntityInstance trackedEntityInstance,
                                             TrackedEntityAttribute trackedEntityAttribute) {
        if (trackedEntityInstance == null || trackedEntityAttribute == null) {
            return null;
        }
        TrackedEntityAttributeValue$Flow trackedEntityInstanceFlow = new Select()
                .from(TrackedEntityAttributeValue$Flow.class)
                .where(Condition.column(TrackedEntityAttributeValue$Flow$Table
                        .TRACKEDENTITYINSTANCE_TRACKEDENTITYINSTANCE).is(trackedEntityInstance)).and(Condition
                        .column(TrackedEntityAttributeValue$Flow$Table.TRACKEDENTITYATTRIBUTEUID)
                        .is(trackedEntityAttribute.getUId())).querySingle();
        return TrackedEntityAttributeValue$Flow.toModel(trackedEntityInstanceFlow);
    }

    @Override
    public List<TrackedEntityAttributeValue> query(TrackedEntityInstance trackedEntityInstance) {
        if (trackedEntityInstance == null) {
            return null;
        }
        List<TrackedEntityAttributeValue$Flow> trackedEntityInstanceFlows = new Select()
                .from(TrackedEntityAttributeValue$Flow.class)
                .where(Condition.column(TrackedEntityAttributeValue$Flow$Table
                        .TRACKEDENTITYINSTANCE_TRACKEDENTITYINSTANCE).is(trackedEntityInstance)).queryList();
        return TrackedEntityAttributeValue$Flow.toModels(trackedEntityInstanceFlows);
    }

    @Override
    public List<TrackedEntityAttributeValue> query(Enrollment enrollment) {
        if (enrollment == null) {
            return null;
        }
        Program program = programStore.queryByUid(enrollment.getProgram());
        List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes =
                program.getProgramTrackedEntityAttributes();
        if (programTrackedEntityAttributes == null) {
            return null;
        }
        List<TrackedEntityAttributeValue$Flow> trackedEntityAttributeValueFlows = new ArrayList<>();
        for (ProgramTrackedEntityAttribute programTrackedEntityAttribute : programTrackedEntityAttributes) {
            TrackedEntityAttributeValue$Flow trackedEntityAttributeValueFlow = new Select()
                    .from(TrackedEntityAttributeValue$Flow.class)
                    .where(Condition.column(TrackedEntityAttributeValue$Flow$Table
                            .TRACKEDENTITYINSTANCE_TRACKEDENTITYINSTANCE).is(enrollment.getTrackedEntityInstance()))
                    .and(Condition.column(TrackedEntityAttributeValue$Flow$Table.TRACKEDENTITYATTRIBUTEUID)
                            .is(programTrackedEntityAttribute.getTrackedEntityAttribute())).querySingle();
            if (trackedEntityAttributeValueFlow != null) {
                trackedEntityAttributeValueFlows.add(trackedEntityAttributeValueFlow);
            }
        }
        return TrackedEntityAttributeValue$Flow.toModels(trackedEntityAttributeValueFlows);
    }
}
