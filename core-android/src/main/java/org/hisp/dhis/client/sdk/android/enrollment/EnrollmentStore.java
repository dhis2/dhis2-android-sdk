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

package org.hisp.dhis.client.sdk.android.enrollment;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.client.sdk.android.common.base.AbsDataStore;
import org.hisp.dhis.client.sdk.android.common.base.IMapper;
import org.hisp.dhis.client.sdk.android.flow.Enrollment$Flow;
import org.hisp.dhis.client.sdk.android.flow.Enrollment$Flow$Table;
import org.hisp.dhis.client.sdk.core.common.IStateStore;
import org.hisp.dhis.client.sdk.core.enrollment.IEnrollmentStore;
import org.hisp.dhis.client.sdk.core.event.IEventStore;
import org.hisp.dhis.client.sdk.core.trackedentity.ITrackedEntityAttributeValueStore;
import org.hisp.dhis.client.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityInstance;

import java.util.List;

public final class EnrollmentStore extends AbsDataStore<Enrollment, Enrollment$Flow> implements IEnrollmentStore {

    public EnrollmentStore(IStateStore stateStore,
                           IMapper<Enrollment, Enrollment$Flow> mapper) {
        super(mapper, stateStore);
    }

    @Override
    public Enrollment queryByUid(String uid) {
        Enrollment$Flow enrollmentFlow = new Select().from(Enrollment$Flow.class)
                .where(Condition.column(Enrollment$Flow$Table
                        .ENROLLMENTUID).is(uid))
                .querySingle();
        return getMapper().mapToModel(enrollmentFlow);
    }

    @Override
    public List<Enrollment> query(Program program, TrackedEntityInstance trackedEntityInstance) {
        List<Enrollment$Flow> enrollmentFlows = new Select()
                .from(Enrollment$Flow.class).where(Condition.column(Enrollment$Flow$Table.
                        PROGRAM).is(program.getUId())).and(Condition.column(Enrollment$Flow$Table.
                        TRACKEDENTITYINSTANCE_TEI).is(trackedEntityInstance.getTrackedEntityInstanceUid())).queryList();
        return getMapper().mapToModels(enrollmentFlows);
    }

    @Override
    public Enrollment queryActiveEnrollment(TrackedEntityInstance trackedEntityInstance, OrganisationUnit organisationUnit, Program program) {
        Enrollment$Flow enrollmentFlow = new Select().from(Enrollment$Flow.class)
                .where(Condition.column(Enrollment$Flow$Table.
                        PROGRAM).is(program.getUId())).and(Condition.column(Enrollment$Flow$Table.
                        TRACKEDENTITYINSTANCE_TEI).is(trackedEntityInstance.getTrackedEntityInstanceUid())).
                        and(Condition.column(Enrollment$Flow$Table.STATUS).is(Enrollment.ACTIVE)).
                        querySingle();
        return getMapper().mapToModel(enrollmentFlow);
    }

    @Override
    public List<Enrollment> query(TrackedEntityInstance trackedEntityInstance) {
        List<Enrollment$Flow> enrollmentFlows = new Select()
                .from(Enrollment$Flow.class).where(Condition.column(Enrollment$Flow$Table.
                        TRACKEDENTITYINSTANCE_TEI).is(trackedEntityInstance.getTrackedEntityInstanceUid())).queryList();
        return getMapper().mapToModels(enrollmentFlows);
    }

    @Override
    public List<Enrollment> query(Program program, OrganisationUnit organisationUnit) {
        List<Enrollment$Flow> enrollmentFlows = new Select()
                .from(Enrollment$Flow.class).where(Condition.column(Enrollment$Flow$Table.
                        PROGRAM).is(program.getUId())).and(Condition.column(Enrollment$Flow$Table.
                        ORGUNIT).is(organisationUnit.getUId())).queryList();
        return getMapper().mapToModels(enrollmentFlows);
    }
}
