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

package org.hisp.dhis.android.sdk.event;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.flow.Event$Flow;
import org.hisp.dhis.android.sdk.flow.Event$Flow$Table;
import org.hisp.dhis.android.sdk.flow.TrackedEntityDataValue$Flow;
import org.hisp.dhis.java.sdk.event.IEventStore;
import org.hisp.dhis.java.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.java.sdk.models.event.Event;
import org.hisp.dhis.java.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.java.sdk.models.program.Program;
import org.hisp.dhis.java.sdk.trackedentity.ITrackedEntityDataValueStore;

import java.util.ArrayList;
import java.util.List;

public final class EventStore implements IEventStore {

    private final ITrackedEntityDataValueStore trackedEntityDataValueStore;

    public EventStore(ITrackedEntityDataValueStore trackedEntityDataValueStore) {
        this.trackedEntityDataValueStore = trackedEntityDataValueStore;
    }

    @Override
    public boolean insert(Event object) {
        Event$Flow eventFlow =
                Event$Flow.fromModel(object);
        eventFlow.insert();
        return true;
    }

    @Override
    public boolean update(Event object) {
        //making sure uid is not overwritten with blank value in case uid was updated from server while event was loaded in memory
        if (object.getUId() == null || object.getUId().isEmpty()) {
            Event$Flow persisted = new Select()
                    .from(Event$Flow.class)
                    .where(Condition.column(Event$Flow$Table
                            .ID).is(object.getId()))
                    .querySingle();
            if (persisted != null) {
                object.setUId(persisted.getEventUid());
            }
        }
        Event$Flow.fromModel(object).update();
        return true;
    }

    @Override
    public boolean save(Event object) {
        //making sure uid is not overwritten with blank value in case uid was updated from
        // server while event was loaded in memory
        if (object.getUId() == null || object.getUId().isEmpty()) {
            Event$Flow persisted = new Select()
                    .from(Event$Flow.class)
                    .where(Condition.column(Event$Flow$Table
                            .ID).is(object.getId()))
                    .querySingle();
            if (persisted != null) {
                object.setUId(persisted.getEventUid());
            }
        }
        Event$Flow.fromModel(object).update();
        Event$Flow eventFlow =
                Event$Flow.fromModel(object);
        eventFlow.save();
        return true;
    }

    @Override
    public boolean delete(Event object) {
        Event$Flow.fromModel(object).delete();
        return true;
    }

    @Override
    public List<Event> queryAll() {
        List<Event$Flow> eventFlows = new Select()
                .from(Event$Flow.class)
                .queryList();
        for (Event$Flow eventFlow : eventFlows) {
            setTrackedEntityDataValues(eventFlow);
        }
        return Event$Flow.toModels(eventFlows);
    }

    @Override
    public Event queryById(long id) {
        Event$Flow eventFlow = new Select()
                .from(Event$Flow.class)
                .where(Condition.column(Event$Flow$Table.ID).is(id))
                .querySingle();
        setTrackedEntityDataValues(eventFlow);
        return Event$Flow.toModel(eventFlow);
    }

    @Override
    public Event queryByUid(String uid) {
        Event$Flow eventFlow = new Select()
                .from(Event$Flow.class)
                .where(Condition.column(Event$Flow$Table.EVENTUID).is(uid))
                .querySingle();
        setTrackedEntityDataValues(eventFlow);
        return Event$Flow.toModel(eventFlow);
    }

    @Override
    public List<Event> query(Enrollment enrollment) {
        List<Event$Flow> eventFlows = new Select()
                .from(Event$Flow.class)
                .where(Condition.column(Event$Flow$Table
                        .ENROLLMENT_ENROLLMENT).is(enrollment)).queryList();
        for (Event$Flow eventFlow : eventFlows) {
            setTrackedEntityDataValues(eventFlow);
        }
        return Event$Flow.toModels(eventFlows);
    }

    @Override
    public List<Event> query(OrganisationUnit organisationUnit, Program program) {
        if (organisationUnit == null || program == null) {
            return new ArrayList<>();
        }
        List<Event$Flow> eventFlows = new Select()
                .from(Event$Flow.class)
                .where(Condition.column(Event$Flow$Table
                        .ORGANISATIONUNITID).is(organisationUnit.getUId()))
                .and(Condition.column(Event$Flow$Table
                        .PROGRAMID).is(program.getUId())).queryList();
        for (Event$Flow eventFlow : eventFlows) {
            setTrackedEntityDataValues(eventFlow);
        }
        return Event$Flow.toModels(eventFlows);
    }

    private void setTrackedEntityDataValues(Event$Flow eventFlow) {
        if (eventFlow == null) {
            return;
        }
        eventFlow.setTrackedEntityDataValues(TrackedEntityDataValue$Flow
                .fromModels(trackedEntityDataValueStore.query(Event$Flow.toModel(eventFlow))));
    }
}
