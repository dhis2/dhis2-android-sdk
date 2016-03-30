package org.hisp.dhis.client.sdk.android.event;

import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.client.sdk.android.api.persistence.flow.EventFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.EventFlow_Table;
import org.hisp.dhis.client.sdk.android.common.AbsIdentifiableObjectDataStore;
import org.hisp.dhis.client.sdk.core.common.IStateStore;
import org.hisp.dhis.client.sdk.core.event.IEventStore;
import org.hisp.dhis.client.sdk.core.trackedentity.ITrackedEntityDataValueStore;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;

import java.util.List;
import java.util.Set;

import static org.hisp.dhis.client.sdk.models.utils.Preconditions.isNull;

public class EventStore extends AbsIdentifiableObjectDataStore<Event, EventFlow>
        implements IEventStore {

    private final ITrackedEntityDataValueStore dataValueStore;

    public EventStore(IStateStore stateStore, ITrackedEntityDataValueStore dataValueStore) {
        super(EventFlow.MAPPER, stateStore);
        this.dataValueStore = dataValueStore;
    }

    @Override
    public boolean insert(Event event) {
        return saveTrackedEntityDataValues(super.insert(event), event);
    }

    @Override
    public boolean update(Event event) {
        return saveTrackedEntityDataValues(super.update(event), event);
    }

    @Override
    public boolean save(Event event) {
        return saveTrackedEntityDataValues(super.save(event), event);
    }

    @Override
    public Event queryById(long id) {
        return queryRelatedTrackedEntityDataValues(super.queryById(id));
    }

    @Override
    public Event queryByUid(String uid) {
        return queryRelatedTrackedEntityDataValues(super.queryByUid(uid));
    }

    @Override
    public List<Event> queryByUids(Set<String> uids) {
        return queryRelatedTrackedEntityDataValues(super.queryByUids(uids));
    }

    @Override
    public List<Event> queryAll() {
        List<TrackedEntityDataValue> trackedEntityDataValues = dataValueStore.queryAll();

        if (trackedEntityDataValues != null && !trackedEntityDataValues.isEmpty()) {

        }
        return null;
    }

    @Override
    public List<Event> query(OrganisationUnit organisationUnit, Program program) {
        isNull(organisationUnit, "OrganisationUnit must not be null");
        isNull(program, "Program must not be null");

        List<EventFlow> eventFlows = new Select()
                .from(EventFlow.class)
                .where(EventFlow_Table
                        .orgUnit.is(organisationUnit.getUId()))
                .and(EventFlow_Table
                        .program.is((program.getUId())))
                .queryList();

        return getMapper().mapToModels(eventFlows);
    }

    private boolean saveTrackedEntityDataValues(boolean isEventSaved, Event event) {
        return false;
    }

    private Event queryRelatedTrackedEntityDataValues(Event event) {
        return null;
    }

    private List<Event> queryRelatedTrackedEntityDataValues(List<Event> events) {
        return null;
    }
}
