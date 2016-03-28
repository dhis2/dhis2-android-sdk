package org.hisp.dhis.client.sdk.android.event;

import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.client.sdk.android.api.persistence.flow.EventFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.EventFlow_Table;
import org.hisp.dhis.client.sdk.android.common.AbsIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.core.event.IEventStore;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.client.sdk.models.program.Program;

import java.util.List;

import static org.hisp.dhis.client.sdk.models.utils.Preconditions.isNull;

public class EventStore extends AbsIdentifiableObjectStore<Event, EventFlow>
        implements IEventStore {

    public EventStore() {
        super(EventFlow.MAPPER);
    }

    @Override
    public List<Event> query(OrganisationUnit organisationUnit, Program program) {
        isNull(organisationUnit, "OrganisationUnit must not be null");
        isNull(program, "Program must not be null");

        List<EventFlow> eventFlows = new Select()
                .from(EventFlow.class)
                .where(EventFlow_Table
                        .organisationUnitId.is(organisationUnit.getUId()))
                .and(EventFlow_Table
                        .programId.is((program.getUId())))
                .queryList();

        return getMapper().mapToModels(eventFlows);
    }
}
