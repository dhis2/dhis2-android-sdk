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

package org.hisp.dhis.client.sdk.android.event;

import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.client.sdk.android.api.persistence.flow.EventFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.EventFlow_Table;
import org.hisp.dhis.client.sdk.android.common.AbsIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.core.common.persistence.ITransactionManager;
import org.hisp.dhis.client.sdk.core.event.IEventStore;
import org.hisp.dhis.client.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.joda.time.DateTime;

import java.util.List;

public class EventStore2 extends AbsIdentifiableObjectStore<Event, EventFlow>
        implements IEventStore {
    private final ITransactionManager transactionManager;

    public EventStore2(ITransactionManager transactionManager) {
        super(EventFlow.MAPPER);

        this.transactionManager = transactionManager;
    }

    @Override
    public List<Event> query(Enrollment enrollment) {
        List<EventFlow> eventFlows = new Select()
                .from(EventFlow.class)
                .where(EventFlow_Table
                        .enrollment.is(enrollment.getUId()))
                .queryList();

        return getMapper().mapToModels(eventFlows);
    }

    @Override
    public List<Event> query(OrganisationUnit organisationUnit, Program program) {
        List<EventFlow> eventFlows = new Select()
                .from(EventFlow.class)
                .where(EventFlow_Table
                        .organisationUnitId.is(organisationUnit.getUId()))
                .and(EventFlow_Table
                        .programId.is((program.getUId())))
                .queryList();

        return getMapper().mapToModels(eventFlows);
    }

    @Override
    public List<Event> query(OrganisationUnit organisationUnit, Program program, DateTime
            startDate, DateTime endDate) {
        List<EventFlow> eventFlows = new Select()
                .from(EventFlow.class)
                .where(EventFlow_Table
                        .organisationUnitId.is(organisationUnit.getUId()))
                .and(EventFlow_Table
                        .programId.is((program.getUId())))
                .and(EventFlow_Table.dueDate.greaterThanOrEq(startDate))
                .and(EventFlow_Table.dueDate.lessThanOrEq(endDate))
                .queryList();

        return getMapper().mapToModels(eventFlows);
    }
}
