package org.hisp.dhis.client.sdk.android.event;

import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.client.sdk.android.api.persistence.flow.EventFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.EventFlow_Table;
import org.hisp.dhis.client.sdk.android.common.AbsIdentifiableObjectDataStore;
import org.hisp.dhis.client.sdk.core.common.IStateStore;
import org.hisp.dhis.client.sdk.core.common.persistence.DbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.IDbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.ITransactionManager;
import org.hisp.dhis.client.sdk.core.event.IEventStore;
import org.hisp.dhis.client.sdk.core.trackedentity.ITrackedEntityDataValueStore;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.client.sdk.models.utils.ModelUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hisp.dhis.client.sdk.models.utils.Preconditions.isNull;

public class EventStore extends AbsIdentifiableObjectDataStore<Event, EventFlow>
        implements IEventStore {

    private final ITrackedEntityDataValueStore dataValueStore;
    private final ITransactionManager transactionManager;

    public EventStore(IStateStore stateStore, ITrackedEntityDataValueStore dataValueStore,
                      ITransactionManager transactionManager) {
        super(EventFlow.MAPPER, stateStore);

        this.dataValueStore = dataValueStore;
        this.transactionManager = transactionManager;
    }

    @Override
    public boolean insert(Event event) {
        boolean isSuccess = super.insert(event);

        if (isSuccess) {
            saveEventDataValues(event);
        }

        return isSuccess;
    }

    @Override
    public boolean update(Event event) {
        boolean isSuccess = super.update(event);

        if (isSuccess) {
            saveEventDataValues(event);
        }

        return isSuccess;
    }

    @Override
    public boolean save(Event event) {
        boolean isSuccess = super.save(event);

        if (isSuccess) {
            saveEventDataValues(event);
        }

        return isSuccess;
    }

    @Override
    public Event queryById(long id) {
        Event event = super.queryById(id);

        List<TrackedEntityDataValue> dataValues = dataValueStore.query(event);
        if (event != null) {
            event.setDataValues(dataValues);
        }

        return event;
    }

    @Override
    public Event queryByUid(String uid) {
        Event event = super.queryByUid(uid);

        List<TrackedEntityDataValue> dataValues = dataValueStore.query(event);
        if (event != null) {
            event.setDataValues(dataValues);
        }

        return event;
    }

    @Override
    public List<Event> queryByUids(Set<String> uids) {
        List<Event> events = super.queryByUids(uids);
        return mapEventsToDataValues(events, dataValueStore
                .query(events));
    }

    @Override
    public List<Event> queryAll() {
        return mapEventsToDataValues(super.queryAll(),
                dataValueStore.queryAll());
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

    private void saveEventDataValues(Event event) {
        List<TrackedEntityDataValue> dataValues = event.getDataValues();
        List<TrackedEntityDataValue> persistedDataValues = dataValueStore.query(event);

        Map<String, TrackedEntityDataValue> updatedDataValuesMap = toMap(dataValues);
        Map<String, TrackedEntityDataValue> persistedDataValueMap = toMap(persistedDataValues);

        List<IDbOperation> dbOperations = new ArrayList<>();
        for (String dataElementUid : updatedDataValuesMap.keySet()) {
            TrackedEntityDataValue updatedDataValue =
                    updatedDataValuesMap.get(dataElementUid);
            TrackedEntityDataValue persistedDataValue =
                    persistedDataValueMap.get(dataElementUid);

            if (persistedDataValue == null) {
                dbOperations.add(DbOperation.with(dataValueStore)
                        .insert(updatedDataValue));
                continue;
            }

            dbOperations.add(DbOperation.with(dataValueStore)
                    .update(updatedDataValue));
            persistedDataValueMap.remove(dataElementUid);
        }

        for (String dataElementUid : persistedDataValueMap.keySet()) {
            TrackedEntityDataValue dataValue =
                    persistedDataValueMap.get(dataElementUid);
            dbOperations.add(DbOperation.with(dataValueStore).delete(dataValue));
        }

        transactionManager.transact(dbOperations);
    }

    private static List<Event> mapEventsToDataValues(
            List<Event> events, List<TrackedEntityDataValue> dataValues) {
        if (events == null || events.isEmpty() ||
                dataValues == null || dataValues.isEmpty()) {
            return events;
        }

        Map<String, Event> eventMap = ModelUtils.toMap(events);
        for (TrackedEntityDataValue dataValue : dataValues) {
            Event event = dataValue.getEvent();
            if (event == null || eventMap.get(event.getUId()) == null) {
                continue;
            }

            if (event.getDataValues() == null) {
                event.setDataValues(new ArrayList<TrackedEntityDataValue>());
            }

            event.getDataValues().add(dataValue);
        }

        return events;
    }

    private static Map<String, TrackedEntityDataValue> toMap(
            Collection<TrackedEntityDataValue> dataValueCollection) {

        Map<String, TrackedEntityDataValue> dataValueMap = new HashMap<>();
        if (dataValueCollection != null && !dataValueCollection.isEmpty()) {
            for (TrackedEntityDataValue dataValue : dataValueCollection) {
                dataValueMap.put(dataValue.getDataElement(), dataValue);
            }
        }

        return dataValueMap;
    }
}
