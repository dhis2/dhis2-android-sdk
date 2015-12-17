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

package org.hisp.dhis.client.sdk.android.event;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.client.sdk.android.common.base.AbsDataStore;
import org.hisp.dhis.client.sdk.android.common.base.IMapper;
import org.hisp.dhis.client.sdk.android.flow.Event$Flow;
import org.hisp.dhis.client.sdk.android.flow.Event$Flow$Table;
import org.hisp.dhis.client.sdk.core.common.IStateStore;
import org.hisp.dhis.client.sdk.core.event.IEventStore;
import org.hisp.dhis.client.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.client.sdk.models.program.Program;

import java.util.ArrayList;
import java.util.List;

public final class EventStore extends AbsDataStore<Event, Event$Flow> implements IEventStore {

    public EventStore(IMapper<Event, Event$Flow> mapper, IStateStore stateStore) {
        super(mapper, stateStore);
    }

    public Event queryByUid(String uid) {
        Event$Flow eventFlow = new Select()
                .from(Event$Flow.class)
                .where(Condition.column(Event$Flow$Table.EVENTUID).is(uid))
                .querySingle();
        return getMapper().mapToModel(eventFlow);
    }

    @Override
    public List<Event> query(Enrollment enrollment) {
        List<Event$Flow> eventFlows = new Select()
                .from(Event$Flow.class)
                .where(Condition.column(Event$Flow$Table
                        .ENROLLMENT_ENROLLMENT).is(enrollment)).queryList();
        return getMapper().mapToModels(eventFlows);
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
        return getMapper().mapToModels(eventFlows);
    }
}
