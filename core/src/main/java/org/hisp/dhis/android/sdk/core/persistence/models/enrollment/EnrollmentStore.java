/*
 *  Copyright (c) 2015, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.sdk.core.persistence.models.enrollment;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.core.persistence.models.flow.Enrollment$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.Enrollment$Flow$Table;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.Event$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.TrackedEntityAttributeValue$Flow;
import org.hisp.dhis.android.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.android.sdk.models.enrollment.IEnrollmentStore;
import org.hisp.dhis.android.sdk.models.event.IEventStore;
import org.hisp.dhis.android.sdk.models.program.Program;
import org.hisp.dhis.android.sdk.models.trackedentityattributevalue.ITrackedEntityAttributeValueStore;
import org.hisp.dhis.android.sdk.models.trackedentityinstance.TrackedEntityInstance;

import java.util.List;

public final class EnrollmentStore implements IEnrollmentStore {

    private final IEventStore eventStore;
    private final ITrackedEntityAttributeValueStore trackedEntityAttributeValueStore;

    public EnrollmentStore(IEventStore eventStore,
                           ITrackedEntityAttributeValueStore trackedEntityAttributeValueStore) {
        this.eventStore = eventStore;
        this.trackedEntityAttributeValueStore = trackedEntityAttributeValueStore;
    }

    @Override
    public void insert(Enrollment object) {
        Enrollment$Flow enrollmentFlow =
                Enrollment$Flow.fromModel(object);
        enrollmentFlow.insert();
    }

    @Override
    public void update(Enrollment object) {
        //making sure uid is not overwritten with blank value in case uid was updated from server while object was loaded in memory
        if(object.getEnrollmentUid() == null || object.getEnrollmentUid().isEmpty()) {
            Enrollment$Flow persisted = new Select().from(Enrollment$Flow.class).where(Condition.column(Enrollment$Flow$Table.ID).is(object.getId())).querySingle();
            if(persisted != null) {
                object.setEnrollmentUid(persisted.getEnrollmentUid());
            }
        }
        Enrollment$Flow.fromModel(object).update();
    }

    @Override
    public void save(Enrollment object) {
        //making sure uid is not overwritten with blank value in case uid was updated from server while object was loaded in memory
        if(object.getEnrollmentUid() == null || object.getEnrollmentUid().isEmpty()) {
            Enrollment$Flow persisted = new Select().from(Enrollment$Flow.class).where(Condition.column(Enrollment$Flow$Table.ID).is(object.getId())).querySingle();
            if(persisted != null) {
                object.setEnrollmentUid(persisted.getEnrollmentUid());
            }
        }
        Enrollment$Flow.fromModel(object).update();
    }

    @Override
    public void delete(Enrollment object) {
        Enrollment$Flow.fromModel(object).delete();
    }

    @Override
    public List<Enrollment> query() {
        List<Enrollment$Flow> enrollmentFlows = new Select()
                .from(Enrollment$Flow.class)
                .queryList();
        for(Enrollment$Flow enrollmentFlow : enrollmentFlows) {
            setEvents(enrollmentFlow);
            setTrackedEntityAttributeValues(enrollmentFlow);
        }
        return Enrollment$Flow.toModels(enrollmentFlows);
    }

    @Override
    public Enrollment query(long id) {
        Enrollment$Flow enrollmentFlow = new Select().from(Enrollment$Flow.class)
                .where(Condition.column(Enrollment$Flow$Table.ID).is(id)).querySingle();
        setEvents(enrollmentFlow);
        setTrackedEntityAttributeValues(enrollmentFlow);
        return Enrollment$Flow.toModel(enrollmentFlow);
    }

    @Override
    public Enrollment query(String uid) {
        Enrollment$Flow enrollmentFlow = new Select().from(Enrollment$Flow.class)
                .where(Condition.column(Enrollment$Flow$Table.ENROLLMENTUID).is(uid)).querySingle();
        setEvents(enrollmentFlow);
        setTrackedEntityAttributeValues(enrollmentFlow);
        return Enrollment$Flow.toModel(enrollmentFlow);
    }

    @Override
    public List<Enrollment> query(Program program, TrackedEntityInstance trackedEntityInstance) {
        List<Enrollment$Flow> enrollmentFlows = new Select()
                .from(Enrollment$Flow.class).where(Condition.column(Enrollment$Flow$Table.
                        PROGRAM).is(program.getUId())).and(Condition.column(Enrollment$Flow$Table.
                        TRACKEDENTITYINSTANCE_TEI).is(trackedEntityInstance)).queryList();
        for(Enrollment$Flow enrollmentFlow : enrollmentFlows) {
            setEvents(enrollmentFlow);
            setTrackedEntityAttributeValues(enrollmentFlow);
        }
        return Enrollment$Flow.toModels(enrollmentFlows);
    }

    @Override
    public Enrollment queryActiveEnrollment(Program program, TrackedEntityInstance trackedEntityInstance) {
        Enrollment$Flow enrollmentFlow = new Select().from(Enrollment$Flow.class)
                .where(Condition.column(Enrollment$Flow$Table.
                        PROGRAM).is(program.getUId())).and(Condition.column(Enrollment$Flow$Table.
                        TRACKEDENTITYINSTANCE_TEI).is(trackedEntityInstance)).
                        and(Condition.column(Enrollment$Flow$Table.STATUS).is(Enrollment.ACTIVE)).
                        querySingle();
        setEvents(enrollmentFlow);
        setTrackedEntityAttributeValues(enrollmentFlow);
        return Enrollment$Flow.toModel(enrollmentFlow);
    }

    @Override
    public List<Enrollment> query(TrackedEntityInstance trackedEntityInstance) {
        List<Enrollment$Flow> enrollmentFlows = new Select()
                .from(Enrollment$Flow.class).where(Condition.column(Enrollment$Flow$Table.
                        TRACKEDENTITYINSTANCE_TEI).is(trackedEntityInstance)).queryList();
        for(Enrollment$Flow enrollmentFlow : enrollmentFlows) {
            setEvents(enrollmentFlow);
            setTrackedEntityAttributeValues(enrollmentFlow);
        }
        return Enrollment$Flow.toModels(enrollmentFlows);
    }

    private void setEvents(Enrollment$Flow enrollmentFlow) {
        if(enrollmentFlow == null) {
            return;
        }
        enrollmentFlow.setEvents(Event$Flow.fromModels(eventStore
                .query(Enrollment$Flow.toModel(enrollmentFlow))));
    }

    private void setTrackedEntityAttributeValues(Enrollment$Flow enrollmentFlow) {
        if(enrollmentFlow == null) {
            return;
        }
        enrollmentFlow.setTrackedEntityAttributeValues(TrackedEntityAttributeValue$Flow
                .fromModels(trackedEntityAttributeValueStore
                        .query(Enrollment$Flow.toModel(enrollmentFlow))));
    }
}
