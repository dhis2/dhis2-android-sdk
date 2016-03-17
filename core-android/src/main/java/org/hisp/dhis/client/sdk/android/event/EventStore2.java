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
    public List<Event> query(OrganisationUnit organisationUnit, Program program, DateTime startDate, DateTime endDate) {
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
