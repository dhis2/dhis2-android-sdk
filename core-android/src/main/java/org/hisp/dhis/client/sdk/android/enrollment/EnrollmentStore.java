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

import org.hisp.dhis.client.sdk.android.api.persistence.flow.EnrollmentFlow;
import org.hisp.dhis.client.sdk.android.common.AbsDataStore;
import org.hisp.dhis.client.sdk.android.common.Mapper;
import org.hisp.dhis.client.sdk.core.common.StateStore;
import org.hisp.dhis.client.sdk.core.enrollment.IEnrollmentStore;
import org.hisp.dhis.client.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityInstance;

import java.util.List;
import java.util.Set;

public final class EnrollmentStore extends AbsDataStore<Enrollment, EnrollmentFlow> implements
        IEnrollmentStore {

    public EnrollmentStore(StateStore stateStore, Mapper<Enrollment, EnrollmentFlow> mapper) {
        super(mapper, stateStore);
    }

    @Override
    public Enrollment queryByUid(String uid) {
//        Enrollment_Flow enrollmentFlow = new Select().from(Enrollment_Flow.class)
//                .where(Condition.column(Enrollment_Flow_Table
//                        .ENROLLMENTUID).is(uid))
//                .querySingle();
//        return getMapper().mapToModel(enrollmentFlow);
        return null;
    }

    @Override
    public List<Enrollment> queryByUids(Set<String> uids) {
        return null;
    }

    @Override
    public boolean areStored(Set<String> uids) {
        return false;
    }

    @Override
    public List<Enrollment> query(Program program, TrackedEntityInstance trackedEntityInstance) {
//        List<Enrollment_Flow> enrollmentFlows = new Select()
//                .from(Enrollment_Flow.class).where(Condition.column(Enrollment_Flow_Table.
//                        PROGRAM).is(program.getUId())).and(Condition.column(Enrollment_Flow_Table.
//                        TRACKEDENTITYINSTANCE_TEI).is(trackedEntityInstance
// .getTrackedEntityInstanceUid())).queryList();
//        return getMapper().mapToModels(enrollmentFlows);
        return null;
    }

    @Override
    public Enrollment queryActiveEnrollment(TrackedEntityInstance trackedEntityInstance,
                                            OrganisationUnit organisationUnit, Program program) {
//        Enrollment_Flow enrollmentFlow = new Select().from(Enrollment_Flow.class)
//                .where(Condition.column(Enrollment_Flow_Table.
//                        PROGRAM).is(program.getUId())).and(Condition.column(Enrollment_Flow_Table.
//                        TRACKEDENTITYINSTANCE_TEI).is(trackedEntityInstance
//                        .getTrackedEntityInstanceUid())).
//                        and(Condition.column(Enrollment_Flow_Table.STATUS).is(Enrollment.ACTIVE)).
//                        querySingle();
//        return getMapper().mapToModel(enrollmentFlow);
        return null;
    }

    @Override
    public List<Enrollment> query(TrackedEntityInstance trackedEntityInstance) {
//        List<Enrollment_Flow> enrollmentFlows = new Select()
//                .from(Enrollment_Flow.class).where(Condition.column(Enrollment_Flow_Table.
//                        TRACKEDENTITYINSTANCE_TEI).is(trackedEntityInstance
//                        .getTrackedEntityInstanceUid())).queryList();
//        return getMapper().mapToModels(enrollmentFlows);
        return null;
    }

    @Override
    public List<Enrollment> query(Program program, OrganisationUnit organisationUnit) {
//        List<Enrollment_Flow> enrollmentFlows = new Select()
//                .from(Enrollment_Flow.class).where(Condition.column(Enrollment_Flow_Table.
//                        PROGRAM).is(program.getUId())).and(Condition.column(Enrollment_Flow_Table.
//                        ORGUNIT).is(organisationUnit.getUId())).queryList();
//        return getMapper().mapToModels(enrollmentFlows);
        return null;
    }
}
